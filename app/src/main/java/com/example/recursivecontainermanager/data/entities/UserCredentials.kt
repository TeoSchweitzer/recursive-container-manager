package com.example.recursivecontainermanager.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserCredentials(
    @PrimaryKey val username: String,
    val password: String
)
