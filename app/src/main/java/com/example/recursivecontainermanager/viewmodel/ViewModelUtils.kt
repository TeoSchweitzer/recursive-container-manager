package com.example.recursivecontainermanager.viewmodel

import androidx.lifecycle.ViewModel
import com.example.recursivecontainermanager.client.MainApi
import com.example.recursivecontainermanager.data.entities.Item
import com.example.recursivecontainermanager.data.entities.Tree

open class ViewModelUtils: ViewModel() {

    protected fun getItemLocation(item: Item, tree: Tree, location: MutableList<Item>): List<Item> {
        location.add(tree.item)
        if (item.id == tree.item.id) return location.reversed()
        if (tree.children == null) return mutableListOf()
        for (child in tree.children) {
            val childResult = getItemLocation(item, child, location)
            if (childResult.isNotEmpty()) return childResult
        }
        return mutableListOf()
    }

    protected fun getSubTree(itemId: String, tree: Tree): Tree? {
        if (itemId == tree.item.id) return tree
        if (tree.children == null) return null
        for (child in tree.children) {
            val childResult = getSubTree(itemId, child)
            if (childResult != null) return childResult
        }
        return null
    }

    protected fun passesNameFilter(nameFilter: String, item: Item): Boolean {
        return (nameFilter == "") || item.name.lowercase().contains(nameFilter.lowercase())
    }

    private fun passesTagFilter(tagFilters: List<String>, item: Item): Boolean {
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

    protected fun getItemContent(filters: List<String>, depth: Int, tree: Tree): List<Item> {
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

    protected fun searchItems(nameFilter: String, tagFilters: List<String>, tree: Tree): List<Item>{
        val toKeep = mutableListOf<Item>()
        if (passesNameFilter(nameFilter, tree.item) && passesTagFilter(tagFilters, tree.item))
            toKeep.add(tree.item)
        if (tree.children == null) return toKeep
        for (child in tree.children) {
            toKeep.addAll(searchItems(nameFilter, tagFilters, child))
        }
        return toKeep
    }

}