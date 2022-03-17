package com.example.recursivecontainermanager.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Tree(
    @PrimaryKey val item: Item,
    val children: List<Tree>?
)
