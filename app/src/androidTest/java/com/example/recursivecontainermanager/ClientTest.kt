package com.example.recursivecontainermanager

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.recursivecontainermanager.client.MainApi
import com.example.recursivecontainermanager.data.entities.UserCredentials
import com.example.recursivecontainermanager.exceptions.InvalidCredentialsException
import com.example.recursivecontainermanager.exceptions.ServerErrorException
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

@RunWith(AndroidJUnit4::class)
class ClientTest {

    private val mockWebServer = MockWebServer()

    @Before
    fun setup() {
        mockWebServer.start(8080)
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                if (request.method == "POST" && request.requestUrl.toString().endsWith("login")) {
                    if (request.bodySize == 0L) fail("POST /login has empty body")
                    lateinit var credentials: UserCredentials
                    try { credentials = jacksonObjectMapper().readValue(request.body.readUtf8())
                    } catch (e: Exception) {fail(e.message)}
                    if (credentials.username == "foo" && credentials.password=="bar") {
                        return MockResponse()
                            .setResponseCode(302)
                            .setHeader("Location", "test.com:8080/tree/az14e-hy85-9drb")
                            .setHeader("Set-Cookie", "session=s14fg852ekil7")
                    }
                    return MockResponse().setResponseCode(403)
                }
                return MockResponse().setResponseCode(404)
            }
        }
    }

    @Test
    fun get_login_with_valid_credentials_returns_302_with_tree_location_and_fresh_session_cookie() {
        runBlocking {
            val result = MainApi.authenticate("foo", "bar")
            assert(result=="test.com:8080/tree/az14e-hy85-9drb")
            assert(MainApi.getSessionCookie()=="s14fg852ekil7")
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

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }
}