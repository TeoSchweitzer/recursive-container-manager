package com.example.recursivecontainermanager.viewmodel

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.*
import com.example.recursivecontainermanager.R
import com.example.recursivecontainermanager.client.MainApi
import com.example.recursivecontainermanager.data.entities.Item
import com.example.recursivecontainermanager.data.entities.Tree
import com.example.recursivecontainermanager.exceptions.*
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.IOException

class MainViewModel: ViewModelUtils() {

    private var itemTree: Tree? = null

    private var currentUser: String? = null

    private val _loadingStatus = MutableLiveData<Int>()
    val loadingStatus: LiveData<Int> = _loadingStatus

    private val _currentItem = MutableLiveData<Item>()
    val currentItem: LiveData<Item> = _currentItem

    private val _recursion = MutableLiveData<Int>(1)
    val recursion: LiveData<Int> = _recursion

    private val _itemContent = MutableLiveData<List<Item>>()
    val itemContent: LiveData<List<Item>> = _itemContent

    private val _itemLocation = MutableLiveData<List<Item>>()
    val itemLocation: LiveData<List<Item>> = _itemLocation

    private val _itemSearch = MutableLiveData<List<Item>>()
    val itemSearch: LiveData<List<Item>> = _itemSearch

    fun fetchItems() {
        if (currentUser==null) {
            _loadingStatus.value = R.string.refresh_done
            return
        }
        viewModelScope.launch {
            executeItemFetching()
        }
    }

    private suspend fun executeItemFetching() {
        _loadingStatus.value = R.string.refresh_loading
        val tree: Tree?
        try { tree = MainApi.getUserItems(currentUser!!) }
        catch (e: ServerErrorException) { _loadingStatus.value = R.string.server_error; return}
        catch (e: IOException) { _loadingStatus.value = R.string.unknown_error; return }
        if (tree == null) { _loadingStatus.value = R.string.no_tree; return }
        itemTree = tree
        if (currentItem.value == null) _currentItem.value = tree!!.item
        else _currentItem.value = getSubTree(currentItem.value!!.id, tree!!)?.item?: tree.item
        _loadingStatus.value = R.string.refresh_done
        updateState()
    }

    fun changeCurrentItem(item: Item) {
        _currentItem.value = item
        updateState()
    }

    private fun updateState() {
        _recursion.value = -1
        _itemContent.value = getItemContent(listOf("!container"), -1, getSubTree(currentItem.value!!.id, itemTree!!)!!)
        _itemLocation.value = getItemLocation(currentItem.value!!, itemTree!!, mutableListOf())
        _itemSearch.value = listOf()
    }

    fun newServerAddress(address: String): Boolean {
        if (address.isBlank()) return false
        MainApi.setServerAddress(address)
        return true
    }

    fun createAccount(username: String, password: String) {
        _loadingStatus.value = R.string.create_account_start
        viewModelScope.launch {
            try {
                MainApi.createAccount(username, password)
            } catch (e: UsernameExistsException) {
                _loadingStatus.value = R.string.create_account_name_taken
            } catch (e: Exception) {
                _loadingStatus.value = R.string.unknown_error
            }
            _loadingStatus.value = R.string.create_account_done
        }
    }

    fun authenticate(username: String, password: String) {
        _loadingStatus.value = R.string.authenticate_start
        viewModelScope.launch {
            try {
                MainApi.authenticate(username, password)
                currentUser = username
                _loadingStatus.value = R.string.authenticate_done
            } catch (e: InvalidCredentialsException) {
                _loadingStatus.value = R.string.authenticate_user_not_found
            } catch (e: Exception) {
                _loadingStatus.value = R.string.unknown_error
            }
        }
    }

    fun newSearchFilter(nameFilter: String, tagFilter: String) {
        if (itemTree == null) return
        _itemSearch.value = searchItems(
            nameFilter,
            splitFilter(tagFilter),
            itemTree!!
        )
    }

    fun newContentFilter(changeDepth: Int, contentFilter: String) {
        val max = getMaxDepth(getSubTree(currentItem.value!!.id, itemTree!!)!!)
        when (changeDepth) {
            -2 -> _recursion.value = 0
            -1 -> if (recursion.value!! > 0) _recursion.value = _recursion.value?.minus(1)
            0  -> return
            +1 -> if (recursion.value!! < max) _recursion.value = _recursion.value?.plus(1)
            +2 -> _recursion.value = max
        }
        _itemContent.value = getItemContent(
            splitFilter(contentFilter),
            recursion.value!!,
            getSubTree(currentItem.value!!.id, itemTree!!)!!
        )
    }

    fun getToken(token: String) {
        _loadingStatus.value = R.string.find_token_loading
        viewModelScope.launch {
            try {
                currentUser = MainApi.getToken(token)
                _loadingStatus.value = R.string.find_token_done
            } catch (e: ResourceNotFoundException) {
                _loadingStatus.value = R.string.find_token_not_found
            } catch (e: ServerErrorException) {
                _loadingStatus.value = R.string.server_error
            } catch (e: Exception) {
                _loadingStatus.value = R.string.unknown_error
            }
        }
    }

    fun getQrCode(context: Context): ImageView {
        var text = "No Data Yet"
        if (currentItem.value != null) text = currentItem.value!!.id
        val view = ImageView(context)
        view.setImageBitmap(
            BarcodeEncoder().encodeBitmap(text, BarcodeFormat.QR_CODE, 400, 400)
        )
        return view
    }

    fun setItemFromLink(itemId: String) {
        viewModelScope.launch {
            executeItemFetching()
            if (itemTree != null) {
                val item = findItemFromId(itemId, itemTree!!)
                if (item != null) changeCurrentItem(item)
                else _loadingStatus.value = R.string.qr_code_match_nothing
            }
        }
    }

    fun sendNewToken(endTime: Long, ownership: String) {
        if (currentItem.value == null) return
        _loadingStatus.value = R.string.new_token_start
        viewModelScope.launch {
            try {
                MainApi.createToken(currentItem.value!!.id, ownership, endTime)
                _loadingStatus.value = R.string.new_token_done
                executeItemFetching()
            } catch (e: ServerErrorException) {
                _loadingStatus.value = R.string.server_error
            } catch (e: Exception) {
                _loadingStatus.value = R.string.unknown_error
            }
        }
    }

    fun itemJsonFormat(): String {
        if (itemTree == null) return ""
        val mapper = jacksonObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
            getSubTree(currentItem.value!!.id,itemTree!!)
        )
    }

    fun deleteItem() {
        if (currentItem.value == null) return
        _loadingStatus.value = R.string.item_edition_start
        viewModelScope.launch {
            try {
                MainApi.deleteItem(currentItem.value!!.id)
                _loadingStatus.value = R.string.item_edition_done
                executeItemFetching()
            } catch (e: EditionConflictException) {
                executeItemFetching()
                _loadingStatus.value = R.string.item_edition_conflict
            } catch (e: Exception) {
                _loadingStatus.value = R.string.unknown_error
            }
        }
    }

    fun addItem(name:String,owners:String,subOwners:String,readOnly:String,tags:String,position:String) {
        if (currentItem.value == null) return
        val newItem = Item("", name, currentItem.value!!.id,
            splitFilter(owners),
            splitFilter(subOwners),
            splitFilter(readOnly),
            splitFilter(tags),
            position.ifBlank {"in"},
            listOf()
        )
        _loadingStatus.value = R.string.item_edition_start
        viewModelScope.launch {
            try {
                MainApi.addItem(newItem)
                _loadingStatus.value = R.string.item_edition_done
                executeItemFetching()
            } catch (e: EditionConflictException) {
                executeItemFetching()
                _loadingStatus.value = R.string.item_edition_conflict
            } catch (e: Exception) {
                _loadingStatus.value = R.string.unknown_error
            }
        }
    }

    fun alterItem(name:String,owners:String,subOwners:String,readOnly:String,tags:String,position:String) {
        if (currentItem.value == null) return
        val newItem = Item(currentItem.value!!.id, name, currentItem.value!!.container,
            splitFilter(owners),
            splitFilter(subOwners),
            splitFilter(readOnly),
            splitFilter(tags),
            position.ifBlank {"in"},
            listOf()
        )
        _loadingStatus.value = R.string.item_edition_start
        viewModelScope.launch {
            try {
                MainApi.alterItem(newItem)
                _loadingStatus.value = R.string.item_edition_done
                executeItemFetching()
            } catch (e: EditionConflictException) {
                executeItemFetching()
                _loadingStatus.value = R.string.item_edition_conflict
            } catch (e: Exception) {
                _loadingStatus.value = R.string.unknown_error
            }
        }
    }
}
