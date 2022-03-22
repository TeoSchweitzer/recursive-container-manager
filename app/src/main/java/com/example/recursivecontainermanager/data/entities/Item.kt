package com.example.recursivecontainermanager.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Item(
    @PrimaryKey var location: String, //uuid
    var name: String,
    var container: String?, //foreign key id
    var owners: List<String>,
    var subOwners: List<String>?,
    var readonly: List<String>?,
    var tags: List<String>?,
    var position: String?, //position description in its container, defaults to "in"
    var tokens: List<Token>?
)