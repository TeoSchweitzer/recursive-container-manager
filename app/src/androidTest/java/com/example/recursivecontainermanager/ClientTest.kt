package com.example.recursivecontainermanager

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.recursivecontainermanager.client.MainApi
import com.example.recursivecontainermanager.data.entities.UserCredentials
import com.example.recursivecontainermanager.exceptions.AccountDeletionFailedException
import com.example.recursivecontainermanager.exceptions.InvalidCredentialsException
import com.example.recursivecontainermanager.exceptions.ServerErrorException
import com.example.recursivecontainermanager.exceptions.UsernameExistsException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import junit.framework.TestCase.fail
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class ClientTest {

    @Test
    fun get_login_with_valid_credentials_returns_302_with_tree_location_and_fresh_session_cookie() {
        runBlocking {
            val result = MainApi.authenticate("ql", "pw")
            assert(result.startsWith("http://gr3-webmobile.herokuapp.com"))
            assert(MainApi.getSessionCookie()!="")
        }
    }

    @Test
    fun get_login_with_invalid_credentials_returns_403() {
        runBlocking {
            try {
                MainApi.authenticate("not", "valid")
                fail("Success: This call should produce an InvalidCredentialsException")
            }
            catch (e: InvalidCredentialsException) {}
            catch (e: ServerErrorException) {
                fail("ServerErrorException: This call should produce an InvalidCredentialsException")
            }
        }
    }

    @Test
    fun create_account_then_authenticate_then_delete_account() {
        val name = "name${Date().time}"
        runBlocking {
            try {
                val result = MainApi.createAccount(name, "testpass")
                assert(result.startsWith("/user/$name"))
                MainApi.authenticate(name,"testpass")
                MainApi.deleteAccount(name)
            }
            catch (e: UsernameExistsException) { fail("UsernameExistsException: $e") }
            catch (e: ServerErrorException) { fail("ServerErrorException: $e") }
            catch (e: AccountDeletionFailedException) { fail("AccountDeletionFailedException: $e") }
        }
    }


    @Test
    fun create_account_then_authenticate_then_delete_with_wrong_session() {
        val name = "name${Date().time}"
        var correctCookie = ""
            runBlocking {
            try {
                val result = MainApi.createAccount(name, "testpass")
                assert(result.startsWith("/user/$name"))
                MainApi.authenticate(name,"testpass")
                correctCookie = MainApi.getSessionCookie()
                MainApi.setSessionCookie("FAKEF079274877E24D6EC9C78CDEFAKE")
                MainApi.deleteAccount(name)
                fail("Account deletion should not work with the wrong session")
            }
            catch (e: UsernameExistsException) { fail("UsernameExistsException: $e") }
            catch (e: ServerErrorException) { fail("ServerErrorException: $e") }
            catch (e: AccountDeletionFailedException) {
                MainApi.setSessionCookie(correctCookie)
                MainApi.deleteAccount(name)
            }
        }
    }
}