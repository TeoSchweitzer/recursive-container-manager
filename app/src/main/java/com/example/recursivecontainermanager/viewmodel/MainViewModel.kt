package com.example.recursivecontainermanager.viewmodel

import androidx.lifecycle.*
import com.example.recursivecontainermanager.R
import com.example.recursivecontainermanager.client.MainApi
import com.example.recursivecontainermanager.data.entities.Item
import com.example.recursivecontainermanager.data.entities.Tree
import com.example.recursivecontainermanager.exceptions.InvalidCredentialsException
import com.example.recursivecontainermanager.exceptions.ServerErrorException
import com.example.recursivecontainermanager.exceptions.UsernameExistsException
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
            _loadingStatus.value = R.string.refresh_loading
            var tree: Tree? = null
            try {
                tree = MainApi.getUserItems(currentUser!!)
            } catch (e: ServerErrorException) {
                _loadingStatus.value = R.string.server_error
                viewModelScope.cancel()
            } catch (e: IOException) {
                _loadingStatus.value = R.string.unknown_error
                viewModelScope.cancel()
            }
            if (tree == null) viewModelScope.cancel()
            itemTree = tree
            if (currentItem.value == null) _currentItem.value = tree!!.item
            else _currentItem.value = getSubTree(currentItem.value!!.id, tree!!)?.item?: tree.item
            _loadingStatus.value = R.string.refresh_done
            updateState()
        }
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
            tagFilter.replace("", "").split(','),
            itemTree!!
        )
    }
}
