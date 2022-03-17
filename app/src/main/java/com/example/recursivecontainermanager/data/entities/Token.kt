package com.example.recursivecontainermanager.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Token(
    @PrimaryKey val toAccess: String,
    val authorizationType: String,
    val end: Long
)
