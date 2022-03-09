package com.example.recursivecontainermanager.data.entities

data class Token(
    val toAccess: String,
    val authorizationType: String,
    val end: Long
)
