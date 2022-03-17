package com.example.recursivecontainermanager.database.dao

import androidx.room.*
import com.example.recursivecontainermanager.data.entities.Tree

@Dao
interface TreeDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addTree(tree: Tree)

    @Query("SELECT * FROM Tree WHERE item IN (:itemUuid)")
    fun getTree(itemUuid: String): Tree

    @Update
    fun alterTree(tree: Tree)

    @Delete
    fun deleteTree(tree: Tree)
}