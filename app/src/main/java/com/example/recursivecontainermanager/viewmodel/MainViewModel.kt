package com.example.recursivecontainermanager.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.widget.ImageView
import androidx.lifecycle.*
import com.example.recursivecontainermanager.R
import com.example.recursivecontainermanager.client.MainApi
import com.example.recursivecontainermanager.data.entities.Item
import com.example.recursivecontainermanager.data.entities.Tree
import com.example.recursivecontainermanager.database.dao.TreeDAO
import com.example.recursivecontainermanager.exceptions.*
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.NullPointerException

const val NO_USER = "%%no_user%%"
const val USER_KEY = "lastUser"
const val SESSION_KEY = "lastSession"
const val ADDRESS_KEY = "lastAddress"
const val ETAG_KEY = "lastItems"

class MainViewModel(private val treeDAO: TreeDAO, private val storage: SharedPreferences): ViewModelUtils() {

    private var itemTree: Tree? = null

    var currentUser: String? = null

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
        if (!storage.contains(USER_KEY)) {
            return
        } else if (storage.getString(USER_KEY, "")!! != NO_USER){
            setUser(
                storage.getString(USER_KEY, "")!!,
                storage.getString(SESSION_KEY, "")!!,
                storage.getString(ETAG_KEY, "")!!
            )
        }
        if (storage.contains(ADDRESS_KEY))
            newServerAddress(storage.getString(ADDRESS_KEY,"")!!)

        viewModelScope.launch(Dispatchers.IO) {
            executeItemFetching()
        }
    }

    private suspend fun executeItemFetching() {
        if (currentUser == null) { _loadingStatus.postValue( R.string.refresh_done); return}
        _loadingStatus.postValue(R.string.refresh_loading)

        val newTree: Tree?
        try {
            newTree = MainApi.getUserItems(currentUser!!)
        }
        catch (e: ServerErrorException) { _loadingStatus.postValue( R.string.server_error); return}
        catch (e: NullPointerException) { _loadingStatus.postValue( R.string.server_error); return}
        catch (e: IOException) { _loadingStatus.postValue( R.string.unknown_error); return }

        if (newTree == null) {
            if (itemTree == null) itemTree = treeDAO.getTree(currentUser!!)
            if (itemTree == null) {
                MainApi.setItemsEtag("")
                executeItemFetching()
                return
            }
        }
        else {
            itemTree = newTree
            treeDAO.addTree(newTree)
            storage.edit().putString(ETAG_KEY, MainApi.getItemsEtag()).apply()
        }
        val item: Item = if (currentItem.value == null) itemTree!!.item
        else getSubTree(currentItem.value!!.location, itemTree!!)?.item?: itemTree!!.item
        _currentItem.postValue(item)
        _loadingStatus.postValue( R.string.refresh_done)
        updateState(item, itemTree!!)
    }

    fun changeCurrentItem(item: Item) {
        _currentItem.postValue( item)
        updateState(item, itemTree!!)
    }

    private fun updateState(item: Item, tree: Tree) {
        _recursion.postValue(getMaxDepth(tree))
        _itemContent.postValue( getItemContent(listOf("!container"), -1, getSubTree(item.location, tree)!!))
        _itemLocation.postValue( getItemLocation(item, tree, mutableListOf()))
        _itemSearch.postValue( listOf())
    }

    fun newServerAddress(address: String): Boolean {
        if (address.isBlank()) return false
        MainApi.setServerAddress(address)
        storage.edit().putString(ADDRESS_KEY, address).apply()
        return true
    }

    fun createAccount(username: String, password: String) {
        _loadingStatus.postValue( R.string.create_account_start)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                MainApi.createAccount(username, password)
            } catch (e: UsernameExistsException) {
                _loadingStatus.postValue( R.string.create_account_name_taken)
            } catch (e: Exception) {
                _loadingStatus.postValue( R.string.unknown_error)
            }
            _loadingStatus.postValue( R.string.create_account_done)
        }
    }

    fun authenticate(username: String, password: String) {
        _loadingStatus.postValue( R.string.authenticate_start)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                MainApi.authenticate(username, password)
                currentUser = username
                storage.edit()
                    .putString(USER_KEY, currentUser!!)
                    .putString(SESSION_KEY, MainApi.getSessionCookie())
                    .apply()
                _loadingStatus.postValue( R.string.authenticate_done)
            } catch (e: InvalidCredentialsException) {
                _loadingStatus.postValue( R.string.authenticate_user_not_found)
            } catch (e: Exception) {
                _loadingStatus.postValue( R.string.unknown_error)
            }
        }
    }

    fun getToken(token: String) {
        _loadingStatus.postValue( R.string.find_token_loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                currentUser = MainApi.getToken(token)
                storage.edit()
                    .putString(USER_KEY, currentUser!!)
                    .putString(SESSION_KEY, MainApi.getSessionCookie())
                    .apply()
                _loadingStatus.postValue( R.string.find_token_done)
            } catch (e: ResourceNotFoundException) {
                _loadingStatus.postValue( R.string.find_token_not_found)
            } catch (e: ServerErrorException) {
                _loadingStatus.postValue( R.string.server_error)
            } catch (e: Exception) {
                _loadingStatus.postValue( R.string.unknown_error)
            }
        }
    }

    private fun setUser(username: String, session: String, items: String) {
        currentUser = username
        MainApi.setSessionCookie(session)
        MainApi.setItemsEtag(items)
    }

    fun newSearchFilter(nameFilter: String, tagFilter: String) {
        if (itemTree == null) return
        _itemSearch.postValue( searchItems(
            nameFilter,
            splitFilter(tagFilter),
            itemTree!!
        ))
    }

    fun newContentFilter(changeDepth: Int, contentFilter: String) {
        val max = getMaxDepth(getSubTree(currentItem.value!!.location, itemTree!!)!!)
        when (changeDepth) {
            -2 -> _recursion.postValue( 0)
            -1 -> if (recursion.value!! > 0) _recursion.postValue( _recursion.value?.minus(1))
            0  -> return
            +1 -> if (recursion.value!! < max) _recursion.postValue( _recursion.value?.plus(1))
            +2 -> _recursion.postValue( max)
        }
        _itemContent.postValue( getItemContent(
            splitFilter(contentFilter),
            recursion.value!!,
            getSubTree(currentItem.value!!.location, itemTree!!)!!
        ))
    }


    fun getQrCode(context: Context): ImageView {
        var text = "No Data Yet"
        if (currentItem.value != null) text = "http://recursivecontainermanager.app/item/${currentItem.value!!.location}"
        val view = ImageView(context)
        view.setImageBitmap(
            BarcodeEncoder().encodeBitmap(text, BarcodeFormat.QR_CODE, 400, 400)
        )
        return view
    }

    fun setItemFromLink(itemId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            executeItemFetching()
            if (itemTree != null) {
                val item = findItemFromId(itemId, itemTree!!)
                if (item != null) changeCurrentItem(item)
                else _loadingStatus.postValue( R.string.qr_code_match_nothing)
            }
        }
    }

    fun sendNewToken(endTime: Long, ownership: String) {
        if (currentItem.value == null) return
        _loadingStatus.postValue( R.string.new_token_start)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                MainApi.createToken(currentItem.value!!.location, ownership, endTime)
                _loadingStatus.postValue( R.string.new_token_done)
                executeItemFetching()
            } catch (e: ServerErrorException) {
                _loadingStatus.postValue( R.string.server_error)
            } catch (e: Exception) {
                _loadingStatus.postValue( R.string.unknown_error)
            }
        }
    }

    fun itemJsonFormat(): String {
        if (itemTree == null) return ""
        val mapper = jacksonObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
            getSubTree(currentItem.value!!.location,itemTree!!)
        )
    }

    fun deleteItem() {
        if (currentItem.value == null) return
        _loadingStatus.postValue( R.string.item_edition_start)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                MainApi.deleteItem(currentItem.value!!.location)
                _loadingStatus.postValue( R.string.item_edition_done)
                executeItemFetching()
            } catch (e: EditionConflictException) {
                executeItemFetching()
                _loadingStatus.postValue( R.string.item_edition_conflict)
            } catch (e: Exception) {
                _loadingStatus.postValue( R.string.unknown_error)
            }
        }
    }

    fun addItem(name:String,owners:String,subOwners:String,readOnly:String,tags:String,position:String) {
        if (currentItem.value == null) return
        val newItem = Item("", name, currentItem.value!!.location,
            splitFilter(owners),
            splitFilter(subOwners),
            splitFilter(readOnly),
            splitFilter(tags),
            position.ifBlank {"in"},
            listOf()
        )
        _loadingStatus.postValue( R.string.item_edition_start)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                MainApi.addItem(newItem)
                _loadingStatus.postValue( R.string.item_edition_done)
                executeItemFetching()
            } catch (e: EditionConflictException) {
                executeItemFetching()
                _loadingStatus.postValue( R.string.item_edition_conflict)
            } catch (e: Exception) {
                _loadingStatus.postValue( R.string.unknown_error)
            }
        }
    }

    fun alterItem(name:String,owners:String,subOwners:String,readOnly:String,tags:String,position:String) {
        if (currentItem.value == null) return
        val newItem = Item(currentItem.value!!.location, name, currentItem.value!!.container,
            splitFilter(owners),
            splitFilter(subOwners),
            splitFilter(readOnly),
            splitFilter(tags),
            position.ifBlank {"in"},
            listOf()
        )
        _loadingStatus.postValue( R.string.item_edition_start)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                MainApi.alterItem(newItem)
                _loadingStatus.postValue( R.string.item_edition_done)
                executeItemFetching()
            } catch (e: EditionConflictException) {
                executeItemFetching()
                _loadingStatus.postValue( R.string.item_edition_conflict)
            } catch (e: Exception) {
                _loadingStatus.postValue( R.string.unknown_error)
            }
        }
    }

    fun importItem(itemJson: String): Item {
        return jacksonObjectMapper().readValue(itemJson)
    }
}

class MainViewModelFactory(private val treeDao: TreeDAO, private val store: SharedPreferences): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(treeDao, store) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}