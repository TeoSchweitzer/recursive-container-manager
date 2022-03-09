package com.example.recursivecontainermanager.data.entities

data class Tree(
    val item: Item,
    val children: List<Tree>?
)
