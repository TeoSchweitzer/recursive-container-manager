package com.example.recursivecontainermanager.client

import com.example.recursivecontainermanager.data.entities.Item
import com.example.recursivecontainermanager.data.entities.Token
import com.example.recursivecontainermanager.data.entities.Tree
import com.example.recursivecontainermanager.data.entities.User
import com.example.recursivecontainermanager.exceptions.*
import com.example.recursivecontainermanager.exceptions.ServerError.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * The base URL will be added by the interceptor
 */
const val BASE_URL = "http://127.0.0.1:8080"
const val SESSION_COOKIE = "JSESSIONID"
const val LOGIN_ERROR_PATH = "/login?error"
private const val REQUEST_TIMEOUT = 120L

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

/**
 * Add Interceptor and timeouts
 */
private val client = OkHttpClient.Builder()
    .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
    .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
    .followRedirects(false)
    .addInterceptor(SessionInterceptor())
    .build()

/**
 * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi
 * object.
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .client(client)
    .build()

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object MainApi {

    private val retrofitService: MainApiInterface by lazy { retrofit.create(MainApiInterface::class.java) }
    private var serverAddress = "http://gr3-webmobile.herokuapp.com"
    private var sessionCookie = ""
    private var itemsEtag = ""

    fun getSessionCookie() = sessionCookie
    fun setSessionCookie(cookie: String) {sessionCookie = cookie}

    fun getServerAddress() = serverAddress
    fun setServerAddress(address: String) {
        serverAddress = if (address.endsWith("/")) address else "$address/"
    }

    /**
     * Takes a name and password, returns the url of the corresponding user, setting a new session cookie on the way.
     * Can throw ServerErrorException (server isn't working properly), and InvalidCredentialsException (name+password match nothing)
     */
    suspend fun authenticate(name: String, password: String): String {
        sessionCookie = ""
        val response = retrofitService.authenticate(name,password)
        if (response.code()!=302) throw ServerErrorException(IMPOSSIBLE_STATUS_RESPONSE, response.code().toString())
        if (response.headers()["Location"]!!.endsWith(LOGIN_ERROR_PATH)) throw InvalidCredentialsException()
        setNewSession(response)
        return response.headers()["Location"]!!
    }

    suspend fun createAccount(name: String, password: String) {
        val response = retrofitService.createAccount(User("firstname","lastname", name, password))
        if (response.code()==409) throw UsernameExistsException()
        if (response.code()!=201) throw ServerErrorException(IMPOSSIBLE_STATUS_RESPONSE, response.code().toString())
    }

    suspend fun deleteAccount(username: String) {
        val response = retrofitService.deleteAccount(username)
        if (response.code()!=200) throw AccountDeletionFailedException()
    }

    suspend fun getUserItems(username: String): Tree? {
        val response = retrofitService.getUserItems(username, itemsEtag)
        if (response.code()==304) return null
        if (response.body() == null) throw ServerErrorException(MALFORMED_RESPONSE, null)
        itemsEtag = response.headers()["Etag"]!!
        return response.body()!!
    }

    suspend fun addItem(item: Item): String {
        val response = retrofitService.addItem(item, itemsEtag)
        if (response.code() == 412) throw EditionConflictException()
        if (response.code()!=201) throw ServerErrorException(IMPOSSIBLE_STATUS_RESPONSE, response.code().toString())
        return response.headers()["Location"]!!.substringAfter("/item/")
    }

    suspend fun alterItem(item: Item) {
        val response = retrofitService.alterItem(item.id, item, itemsEtag)
        if (response.code() == 412) throw EditionConflictException()
    }

    suspend fun deleteItem(itemUuid: String) {
        val response = retrofitService.deleteItem(itemUuid, itemsEtag)
        if (response.code() == 412) throw EditionConflictException()
    }

    suspend fun createToken(itemUuid: String, authorizationType: String, end: Date): String {
        val token = Token(itemUuid, authorizationType, end.time)
        val response = retrofitService.createToken(token)
        if (response.code()!=201) throw ServerErrorException(IMPOSSIBLE_STATUS_RESPONSE, response.code().toString())
        return response.headers()["Location"]!!.substringAfter("/token/")
    }

    suspend fun getToken(code: String){
        val response = retrofitService.getToken(code)
        if (response.code()!=200) throw ServerErrorException(IMPOSSIBLE_STATUS_RESPONSE, response.code().toString())
        if (sessionCookie=="") setNewSession(response)
    }

    private fun setNewSession(resp: Response<Unit>) {
        var session = ""
        for (header in resp.headers()){
            if (header.first=="Set-Cookie" && header.second.startsWith("$SESSION_COOKIE=")) {
                session = header.second.substringAfter("$SESSION_COOKIE=").split(';')[0]
                break
            }
        }
        if (session=="") throw ServerErrorException(NO_SESSION_COOKIE, null)
        sessionCookie = session
    }
}