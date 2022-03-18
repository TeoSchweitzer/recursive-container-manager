package com.example.recursivecontainermanager.database.dao

import androidx.room.*
import com.example.recursivecontainermanager.data.entities.Token

@Dao
interface TokenDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addToken(token: Token)

    @Query("SELECT * FROM Token WHERE tokenCode IN (:access)")
    fun getToken(access: String): Token

    @Update
    fun alterToken(token: Token)

    @Delete
    fun deleteToken(token: Token)
}