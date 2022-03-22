package com.example.recursivecontainermanager.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Item(
    @PrimaryKey var location: String,
    var name: String,
    var container: String?,
    var owners: List<String>,
    var subOwners: List<String>?,
    var readonly: List<String>?,
    var tags: List<String>?,
    var position: String?,
    var tokens: List<Token>?
)

