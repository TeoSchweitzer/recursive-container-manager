package com.example.recursivecontainermanager.database.dao

import androidx.room.*
import com.example.recursivecontainermanager.data.entities.Item

@Dao
interface ItemDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addItem(item: Item)

    @Query("SELECT * FROM Item WHERE id IN (:itemUuid)")
    fun getItem(itemUuid: String): Item

    @Update
    fun alterItem(item: Item)

    @Delete
    fun deleteItem(item: Item)
}