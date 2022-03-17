package com.example.recursivecontainermanager.DAOTest

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.recursivecontainermanager.dao.DataBase
import com.example.recursivecontainermanager.data.entities.Item
import com.example.recursivecontainermanager.data.entities.Token
import com.example.recursivecontainermanager.database.dao.ItemDAO
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class ItemDAOTest {
    private lateinit var itemDAO: ItemDAO
    private lateinit var db: DataBase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, DataBase::class.java).build()
        itemDAO = db.itemDao()!!
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    // ITEM
    @Test
    @Throws(Exception::class)
    fun writeItemAndRead() {
        val token1: Token = Token(
            "uuid1",
            "type1",
            200
        )
        val token2: Token = Token(
            "uuid2",
            "type2",
            300
        )
        val item: Item = Item(
            "uuid",
            "name",
            "location",
            "container",
            arrayListOf("owner1", "owner2"),
            arrayListOf("subowner1", "subowner2"),
            null,
            arrayListOf("tag1", "tag2"),
            "position",
            arrayListOf(token1,token2)
        )
        itemDAO.addItem(item)
        val itemRead = itemDAO.getItem(item.id)
        assertThat(item, equalTo(itemRead))
    }

    @Test
    @Throws(Exception::class)
    fun writeItemAndAlter() {
        val token1: Token = Token(
            "uuid1",
            "type1",
            200
        )
        val token2: Token = Token(
            "uuid2",
            "type2",
            300
        )
        val item: Item = Item(
            "uuid",
            "name",
            "location",
            "container",
            arrayListOf("owner1", "owner2"),
            arrayListOf("subowner1", "subowner2"),
            null,
            arrayListOf("tag1", "tag2"),
            "position",
            arrayListOf(token1,token2)
        )
        val newitem: Item = Item(
            "uuid",
            "name2",
            "location2",
            "container2",
            arrayListOf("owner3", "owner4"),
            arrayListOf("subowner3", "subowner4"),
            arrayListOf("read3", "read4"),
            arrayListOf("tag3", "tag4"),
            "position2",
            arrayListOf(token2,token1)
        )
        itemDAO.addItem(item)
        itemDAO.alterItem(newitem)
        val itemRead = itemDAO.getItem(item.id)
        assertThat(newitem, equalTo(itemRead))
    }

    @Test
    @Throws(Exception::class)
    fun writeItemAndRewrite() {
        val token1: Token = Token(
            "uuid1",
            "type1",
            200
        )
        val token2: Token = Token(
            "uuid2",
            "type2",
            300
        )
        val item: Item = Item(
            "uuid",
            "name",
            "location",
            "container",
            arrayListOf("owner1", "owner2"),
            arrayListOf("subowner1", "subowner2"),
            null,
            arrayListOf("tag1", "tag2"),
            "position",
            arrayListOf(token1,token2)
        )
        val newitem: Item = Item(
            "uuid",
            "name2",
            "location2",
            "container2",
            arrayListOf("owner3", "owner4"),
            arrayListOf("subowner3", "subowner4"),
            null,
            arrayListOf("tag3", "tag4"),
            "position2",
            arrayListOf(token2,token1)
        )
        itemDAO.addItem(item)
        itemDAO.addItem(newitem)
        val itemRead = itemDAO.getItem(item.id)
        assertThat(newitem, equalTo(itemRead))
    }

    @Test
    @Throws(Exception::class)
    fun writeItemAndDelete() {
        val token1: Token = Token(
            "uuid1",
            "type1",
            200
        )
        val token2: Token = Token(
            "uuid2",
            "type2",
            300
        )
        val item: Item = Item(
            "uuid",
            "name",
            "location",
            "container",
            arrayListOf("owner1", "owner2"),
            arrayListOf("subowner1", "subowner2"),
            null,
            arrayListOf("tag1", "tag2"),
            "position",
            arrayListOf(token1,token2)
        )
        itemDAO.addItem(item)
        itemDAO.deleteItem(item)
        val itemRead = itemDAO.getItem(item.id)
        assertThat(null, equalTo(itemRead))
    }
}