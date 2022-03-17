package com.example.recursivecontainermanager.data.entities

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Item(
    @PrimaryKey val id: String, //uuid
    var name: String,
    var location: String, //URL
    var container: String, //foreign key id
    var owners: List<String>,
    var subOwners: List<String>?,
    var readonly: List<String>?,
    var tags: List<String>?,
    var position: String? //position description in its container, defaults to "in"
)