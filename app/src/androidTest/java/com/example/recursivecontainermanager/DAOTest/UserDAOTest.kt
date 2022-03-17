package com.example.recursivecontainermanager.DAOTest

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.recursivecontainermanager.dao.DataBase
import com.example.recursivecontainermanager.data.entities.UserCredentials
import com.example.recursivecontainermanager.database.dao.UserCredentialsDAO
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class UserDAOTest {
    private lateinit var userCredentialsDAO: UserCredentialsDAO
    private lateinit var db: DataBase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, DataBase::class.java).build()
        userCredentialsDAO = db.userCredentialsDao()!!
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    // USER
    @Test
    @Throws(Exception::class)
    fun writeUserAndRead() {
        val user: UserCredentials = UserCredentials(
            "name",
            "password"
        )
        userCredentialsDAO.addUser(user)
        val userRead = userCredentialsDAO.getUser(user.username)
        assertThat(user, equalTo(userRead))
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndAlter() {
        val user: UserCredentials = UserCredentials(
            "name",
            "password"
        )
        val newuser: UserCredentials = UserCredentials(
            "name",
            "password2"
        )
        userCredentialsDAO.addUser(user)
        userCredentialsDAO.alterUser(newuser)
        val userRead = userCredentialsDAO.getUser(user.username)
        assertThat(newuser, equalTo(userRead))
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndRewrite() {
        val user: UserCredentials = UserCredentials(
            "name",
            "password"
        )
        val newuser: UserCredentials = UserCredentials(
            "name",
            "password2"
        )
        userCredentialsDAO.addUser(user)
        userCredentialsDAO.addUser(newuser)
        val userRead = userCredentialsDAO.getUser(user.username)
        assertThat(newuser, equalTo(userRead))
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndDelete() {
        val user: UserCredentials = UserCredentials(
            "name",
            "password"
        )
        userCredentialsDAO.addUser(user)
        userCredentialsDAO.deleteUser(user)
        val userRead = userCredentialsDAO.getUser(user.username)
        assertThat(null, equalTo(userRead))
    }
}