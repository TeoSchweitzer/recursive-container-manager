package com.example.recursivecontainermanager.viewmodel

import androidx.lifecycle.*
import com.example.recursivecontainermanager.client.MainApi
import com.example.recursivecontainermanager.data.entities.Item
import com.example.recursivecontainermanager.data.entities.Tree
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.IOException

enum class LoadingStatus {
    LOADING,
    SUCCESS,
    FAILURE
}

class MainViewModel: ViewModel() {

    private var itemTree: Tree? = null

    private var username: String? = null

    private val _loadingStatus = MutableLiveData<LoadingStatus>()
    val loadingStatus: LiveData<LoadingStatus> = _loadingStatus

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
        if (username==null) {
            _loadingStatus.value = LoadingStatus.SUCCESS
            return
        }
        viewModelScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING
            var tree: Tree? = null
            try {
                tree = MainApi.getUserItems(username!!)
            } catch (e: IOException) {
                _loadingStatus.value = LoadingStatus.FAILURE
                viewModelScope.cancel()
            }
            if (tree == null) viewModelScope.cancel()
            itemTree = tree
            if (currentItem.value == null) _currentItem.value = tree!!.item
            else _currentItem.value = getSubTree(currentItem.value!!.id, tree!!)?.item?: tree.item
            _loadingStatus.value = LoadingStatus.SUCCESS
            updateState()
        }
    }

    private fun updateState() {
        _recursion.value = -1
        _itemContent.value = getItemContent(listOf("!container"), -1, getSubTree(currentItem.value!!.id, itemTree!!)!!)
        _itemLocation.value = getItemLocation(currentItem.value!!, itemTree!!, mutableListOf())
        _itemSearch.value = listOf()
    }

    private fun getSubTree(itemId: String, tree: Tree): Tree? {
        if (itemId == tree.item.id) return tree
        if (tree.children == null) return null
        for (child in tree.children) {
            val childResult = getSubTree(itemId, child)
            if (childResult != null) return childResult
        }
        return null
    }

    private fun getItemContent(filters: List<String>, depth: Int, tree: Tree): List<Item> {
        val collection = mutableListOf<Item>()
        if (depth == 0 || tree.children == null) return collection
        for (child in tree.children) {
            if (passesTagFilter(filters, child.item)) {
                collection.add(child.item)
            }
            if (depth > 0) collection.addAll(getItemContent(filters, depth-1, child))
            else collection.addAll(getItemContent(filters, depth, child))
        }
        return collection
    }

    fun passesNameFilter(nameFilter: String, item: Item): Boolean {
        return (nameFilter == "") || item.name.lowercase().contains(nameFilter.lowercase())
    }

    fun passesTagFilter(tagFilters: List<String>, item: Item): Boolean {
        for (tagFilter in tagFilters) {
            if (tagFilter == "") continue
            var tagFound = false
            for (tag in item.tags?:listOf()) {
                if (tag.lowercase().contains(tagFilter.lowercase()) && tagFilter.startsWith('!')) return false
                if (tag.lowercase().contains(tagFilter.lowercase())) tagFound = true
            }
            if (!tagFound) return false
        }
        return true
    }

    private fun getItemLocation(item: Item, tree: Tree, location: MutableList<Item>): List<Item> {
        location.add(tree.item)
        if (item.id == tree.item.id) return location.reversed()
        if (tree.children == null) return mutableListOf()
        for (child in tree.children) {
            val childResult = getItemLocation(item, child, location)
            if (childResult.isNotEmpty()) return childResult
        }
        return mutableListOf()
    }
}