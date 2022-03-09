package com.example.recursivecontainermanager.data.entities

import androidx.annotation.NonNull
import java.io.Serializable

data class Item(
    val id: String, //uuid
    var name: String,
    var location: String, //URL
    var container: String, //foreign key id
    var owners: List<String>,
    var subOwners: List<String>?,
    var readonly: List<String>?,
    var tags: List<String>?,
    var position: String? //position description in its container, defaults to "in"
)