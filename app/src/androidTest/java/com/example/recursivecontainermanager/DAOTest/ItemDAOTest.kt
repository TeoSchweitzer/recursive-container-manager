package com.example.recursivecontainermanager.DAOTest

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.recursivecontainermanager.dao.DataBase
import com.example.recursivecontainermanager.data.entities.Item
import com.example.recursivecontainermanager.database.dao.ItemDAO
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class DAOTest {
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
        val item: Item = Item(
            "uuid",
            "name",
            "location",
            "container",
            arrayListOf("owner1", "owner2"),
            arrayListOf("subowner1", "subowner2"),
            arrayListOf("read1", "read2"),
            arrayListOf("tag1", "tag2"),
            "position"
        )
        itemDAO.addItem(item)
        val itemRead = itemDAO.getItem(item.id)
        assertThat(item, equalTo(itemRead))
    }

    @Test
    @Throws(Exception::class)
    fun writeItemAndAlter() {
        val item: Item = Item(
            "uuid",
            "name",
            "location",
            "container",
            arrayListOf("owner1", "owner2"),
            arrayListOf("subowner1", "subowner2"),
            arrayListOf("read1", "read2"),
            arrayListOf("tag1", "tag2"),
            "position"
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
            "position2"
        )
        itemDAO.addItem(item)
        itemDAO.alterItem(newitem)
        val itemRead = itemDAO.getItem(item.id)
        assertThat(newitem, equalTo(itemRead))
    }

    @Test
    @Throws(Exception::class)
    fun writeItemAndRewrite() {
        val item: Item = Item(
            "uuid",
            "name",
            "location",
            "container",
            arrayListOf("owner1", "owner2"),
            arrayListOf("subowner1", "subowner2"),
            arrayListOf("read1", "read2"),
            arrayListOf("tag1", "tag2"),
            "position"
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
            "position2"
        )
        itemDAO.addItem(item)
        itemDAO.addItem(newitem)
        val itemRead = itemDAO.getItem(item.id)
        assertThat(newitem, equalTo(itemRead))
    }

    @Test
    @Throws(Exception::class)
    fun writeItemAndDelete() {
        val item: Item = Item(
            "uuid",
            "name",
            "location",
            "container",
            arrayListOf("owner1", "owner2"),
            arrayListOf("subowner1", "subowner2"),
            arrayListOf("read1", "read2"),
            arrayListOf("tag1", "tag2"),
            "position"
        )
        itemDAO.addItem(item)
        itemDAO.deleteItem(item)
        val itemRead = itemDAO.getItem(item.id)
        assertThat(null, equalTo(itemRead))
    }
}