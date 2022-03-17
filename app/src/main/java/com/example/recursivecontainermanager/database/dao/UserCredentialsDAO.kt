package com.example.recursivecontainermanager.database.dao

import androidx.room.*
import com.example.recursivecontainermanager.data.entities.UserCredentials

@Dao
interface UserCredentialsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addUser(item: UserCredentials)

    @Query("SELECT * FROM UserCredentials WHERE username IN (:username)")
    fun getUser(username: String): UserCredentials

    @Update
    fun alterUser(user: UserCredentials)

    @Delete
    fun deleteUser(user: UserCredentials)
}