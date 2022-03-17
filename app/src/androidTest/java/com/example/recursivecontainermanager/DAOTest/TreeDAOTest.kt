package com.example.recursivecontainermanager.DAOTest

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.recursivecontainermanager.dao.DataBase
import com.example.recursivecontainermanager.data.entities.Item
import com.example.recursivecontainermanager.data.entities.Token
import com.example.recursivecontainermanager.data.entities.Tree
import com.example.recursivecontainermanager.database.dao.TreeDAO
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class TreeDAOTest {
    private lateinit var treeDAO: TreeDAO
    private lateinit var db: DataBase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, DataBase::class.java).build()
        treeDAO = db.treeDao()!!
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeTreeZeroGenerationAndRead() {
        val token1: Token = Token(
            "uuid",
            "type",
            200
        )
        val token2: Token = Token(
            "uuid",
            "type",
            200
        )
        val item: Item = Item(
            "uuid",
            "name",
            "location",
            "container",
            arrayListOf("owner1", "owner2"),
            arrayListOf("subowner1", "subowner2"),
            arrayListOf("read1", "read2"),
            arrayListOf("tag1", "tag2"),
            "position",
            arrayListOf(token1,token2)
        )
        val tree: Tree = Tree(
            item,
            null
        )
        treeDAO.addTree(tree)
        val treeRead = treeDAO.getTree(tree.item.id)
        assertThat(tree, equalTo(treeRead))
    }

    @Test
    @Throws(Exception::class)
    fun writeTreeOneGenerationAndRead() {
        val token1: Token = Token(
            "uuid",
            "type",
            200
        )
        val token2: Token = Token(
            "uuid",
            "type",
            200
        )
        val item1: Item = Item(
            "uuid1",
            "name1",
            "location1",
            "container1",
            arrayListOf("owner1", "owner2"),
            arrayListOf("subowner1", "subowner2"),
            arrayListOf("read1", "read2"),
            arrayListOf("tag1", "tag2"),
            "position1",
            arrayListOf(token1,token2)
        )
        val item2: Item = Item(
            "uuid2",
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
        val treechild1: Tree = Tree(
            item1,
            null
        )
        val treechild2: Tree = Tree(
            item2,
            null
        )
        val tree: Tree = Tree(
            item1,
            listOf(treechild1, treechild2)
        )
        treeDAO.addTree(tree)
        val treeRead = treeDAO.getTree(tree.item.id)
        assertThat(tree, equalTo(treeRead))
    }

    @Test
    @Throws(Exception::class)
    fun writeTreeTwoGenerationAndRead() {
        val token1: Token = Token(
            "uuid",
            "type",
            200
        )
        val token2: Token = Token(
            "uuid",
            "type",
            200
        )
        val item1: Item = Item(
            "uuid1",
            "name1",
            "location1",
            "container1",
            arrayListOf("owner1", "owner2"),
            arrayListOf("subowner1", "subowner2"),
            arrayListOf("read1", "read2"),
            arrayListOf("tag1", "tag2"),
            "position1",
            arrayListOf(token1,token2)
        )
        val item2: Item = Item(
            "uuid2",
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
        val treelittlechild1: Tree = Tree(
            item1,
            null
        )
        val treelittlechild2: Tree = Tree(
            item2,
            null
        )
        val treechild1: Tree = Tree(
            item1,
            listOf(treelittlechild1, treelittlechild2)
        )
        val treechild2: Tree = Tree(
            item2,
            listOf(treelittlechild2, treelittlechild1)
        )
        val tree: Tree = Tree(
            item1,
            listOf(treechild1, treechild2)
        )
        treeDAO.addTree(tree)
        val treeRead = treeDAO.getTree(tree.item.id)
        assertThat(tree, equalTo(treeRead))
    }

    @Test
    @Throws(Exception::class)
    fun writeTreeAndAlter() {
    }

    @Test
    @Throws(Exception::class)
    fun writeTreeAndRewrite() {
    }

    @Test
    @Throws(Exception::class)
    fun writeTreeAndDelete() {
    }
}