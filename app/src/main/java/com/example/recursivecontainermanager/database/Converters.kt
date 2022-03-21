package com.example.recursivecontainermanager.database

import android.content.Context
import android.util.Log
import androidx.room.TypeConverter
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.example.recursivecontainermanager.MainApplication
import com.example.recursivecontainermanager.data.entities.Item
import com.example.recursivecontainermanager.data.entities.Token
import com.example.recursivecontainermanager.data.entities.Tree
import com.example.recursivecontainermanager.database.dao.ItemDAO
import com.example.recursivecontainermanager.database.dao.TreeDAO

class Converters {
    private lateinit var itemDAO: ItemDAO
    private lateinit var treeDAO: TreeDAO
    private lateinit var db: DataBase


    @TypeConverter
    fun listToString(list : List<String>?): String? {
        if(list == null) return null
        return list.joinToString(",")
    }

    @TypeConverter
    fun stringToList(string : String?): List<String>? {
        if (string == null) return null
        val value : List<String>? = string!!.split(",")
        return value
    }

    @TypeConverter
    fun tokensToString(list : List<Token>?): String? {
        if(list == null) return null
        var value : String = ""
        for(i in 0..list.size-2){
            value += list[i].tokenCode + "," +
                    list[i].authorizationType + "," +
                    list[i].end + ":"
        }
        value += list[list.size-1].tokenCode + "," +
                list[list.size-1].authorizationType + "," +
                list[list.size-1].end
        return value
    }

    @TypeConverter
    fun stringToTokens(string : String?): List<Token>? {
        if (string == null) return null
        var listToken: List<Token> = listOf()
        val value : List<String>? = string!!.split(":")
        for (v in value!!){
            val tokenvalue : List<String> = v!!.split(",")
            val token: Token = Token(
                tokenvalue[0],
                tokenvalue[1],
                tokenvalue[2].toLong()
            )
            listToken += token
        }
        return listToken
    }

    @TypeConverter
    fun itemToString(item : Item): String {
        db = DataBase.invoke(MainApplication.context!!)
        itemDAO = db.itemDao()!!
        itemDAO.addItem(item)
        Log.i("test_item_add",item.location +" "+ item.owners[0])
        return item.location
    }

    @TypeConverter
    fun stringToItem(string : String): Item {
        db = DataBase.invoke(MainApplication.context!!)
        itemDAO = db.itemDao()!!
        Log.i("test_item_get",string)
        val item: Item = itemDAO.getItem(string) //TODO return null trouver pourquoi
        Log.i("test_item_get",string + " - " + item)
        return item
    }

    @TypeConverter
    fun listTreeToString(list : List<Tree>?): String? {
        db = DataBase.invoke(MainApplication.context!!)
        treeDAO = db.treeDao()
        if(list == null) return null
        var value = ""
        for (tree in list){
            treeDAO.addTree(tree)
            value += tree.item.location + ","
        }
        return value.substringBeforeLast(',')
    }

    @TypeConverter
    fun stringToListTree(string : String?): List<Tree>? {
        db = DataBase.invoke(MainApplication.context!!)
        treeDAO = db.treeDao()!!
        if(string == null) return null
        var tree: Tree
        val idItems : List<String>? = string.split(",")
        var listTree: List<Tree> = listOf()
        for (id in idItems!!){
            tree = treeDAO.getTree(id)
            listTree += tree
        }
        return listTree
    }
}