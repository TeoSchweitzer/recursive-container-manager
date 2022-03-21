package com.example.recursivecontainermanager.DAOTest

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.recursivecontainermanager.database.DataBase
import com.example.recursivecontainermanager.data.entities.Token
import com.example.recursivecontainermanager.database.dao.TokenDAO
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class TokenDAOTest {
    private lateinit var tokenDAO: TokenDAO
    private lateinit var db: DataBase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, DataBase::class.java).build()
        tokenDAO = db.tokenDao()!!
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    // TOKEN
    @Test
    @Throws(Exception::class)
    fun writeTokenAndRead() {
        val token: Token = Token(
            "uuid",
            "type",
            200
        )
        tokenDAO.addToken(token)
        val tokenRead = tokenDAO.getToken(token.tokenCode)
        assertThat(token, equalTo(tokenRead))
    }

    @Test
    @Throws(Exception::class)
    fun writeTokenAndAlter() {
        val token: Token = Token(
            "uuid",
            "type",
            200
        )
        val newtoken: Token = Token(
            "uuid",
            "type2",
            3000
        )
        tokenDAO.addToken(token)
        tokenDAO.alterToken(newtoken)
        val tokenRead = tokenDAO.getToken(token.tokenCode)
        assertThat(newtoken, equalTo(tokenRead))
    }

    @Test
    @Throws(Exception::class)
    fun writeTokenAndRewrite() {
        val token: Token = Token(
            "uuid",
            "type",
            200
        )
        val newtoken: Token = Token(
            "uuid",
            "type2",
            3000
        )
        tokenDAO.addToken(token)
        tokenDAO.addToken(newtoken)
        val tokenRead = tokenDAO.getToken(token.tokenCode)
        assertThat(newtoken, equalTo(tokenRead))
    }

    @Test
    @Throws(Exception::class)
    fun writeTokenAndDelete() {
        val token: Token = Token(
            "uuid",
            "type",
            200
        )
        tokenDAO.addToken(token)
        tokenDAO.deleteToken(token)
        val tokenRead = tokenDAO.getToken(token.tokenCode)
        assertThat(null, equalTo(tokenRead))
    }
}