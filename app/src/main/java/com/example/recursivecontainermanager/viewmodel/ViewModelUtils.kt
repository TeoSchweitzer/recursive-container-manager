package com.example.recursivecontainermanager.viewmodel

import androidx.lifecycle.ViewModel
import com.example.recursivecontainermanager.data.entities.Item
import com.example.recursivecontainermanager.data.entities.Tree
import java.util.Collections.max

open class ViewModelUtils: ViewModel() {

    protected fun getItemLocation(item: Item, tree: Tree, location: MutableList<Item>): List<Item> {
        location.add(tree.item)
        if (item.location == tree.item.location) return location.reversed()
        if (tree.children == null) return mutableListOf()
        for (child in tree.children) {
            val childResult = getItemLocation(item, child, location)
            if (childResult.isNotEmpty()) return childResult
        }
        return mutableListOf()
    }

    protected fun getSubTree(itemId: String, tree: Tree): Tree? {
        if (itemId == tree.item.location) return tree
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
            var filter = tagFilter
            if (filter.startsWith('!')) filter = filter.substring(1)
            var tagFound = false//tagFilter.startsWith('!')
            for (tag in item.tags?:listOf()) {
                if (tag.lowercase().contains(filter.lowercase())) {
                    tagFound = true
                    break
                }
            }
            if ((!tagFound && !tagFilter.startsWith('!')) ||
                (tagFound && tagFilter.startsWith('!')))
                    return false
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

    protected fun splitFilter(filter: String): List<String> {
        return filter.replace(" ", "").split(',')
    }

    protected fun getMaxDepth(tree: Tree): Int {
        if (tree.children == null) return 0
        val childMax = mutableListOf<Int>()
        for (child in tree.children) { childMax.add(1 + getMaxDepth(child)) }
        var max = 0
        for (candidate in childMax) {
            if (candidate>max) max = candidate
        }
        return max
    }

    protected fun findItemFromId(itemId: String, tree: Tree): Item? {
        if (tree.item.location == itemId) return tree.item
        if (tree.children == null) return null
        for (child in tree.children) {
            val item = findItemFromId(itemId, child)
            if (item != null) return item
        }
        return null
    }
}