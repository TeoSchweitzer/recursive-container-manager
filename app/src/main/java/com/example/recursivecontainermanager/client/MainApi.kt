package com.example.recursivecontainermanager.client

import com.example.recursivecontainermanager.data.entities.UserCredentials
import com.example.recursivecontainermanager.exceptions.ServerError.*
import com.example.recursivecontainermanager.exceptions.ServerErrorException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit


/**
 * The base URL will be added by the interceptor
 */
const val BASE_URL = "http://127.0.0.1:8080"
private const val REQUEST_TIMEOUT = 10L

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

    val retrofitService: MainApiInterface by lazy { retrofit.create(MainApiInterface::class.java) }
    private var serverAddress = "http://127.0.0.1:8080"
    private var sessionCookie = ""

    fun getSessionCookie() = sessionCookie

    fun getServerAddress() = serverAddress
    fun setServerAddress(address: String) {
        serverAddress = if (address.endsWith("/")) address else "$address/"
    }

    /**
     * Takes a name and password, returns the url of the corresponding user, setting a new session cookie on the way.
     * Can throw ServerErrorException (server isn't working properly), and InvalidCredentialsException (name+password match nothing)
     */
    suspend fun authenticate(name: String, password: String): String {
        val response = retrofitService.authenticate(UserCredentials(name, password))
        if (response.code()!=302) throw ServerErrorException("/login", IMPOSSIBLE_STATUS_RESPONSE, response.code().toString())
        setNewSession(response )
        return response.headers()["Location"]!!
    }



    private fun setNewSession(resp: Response<Unit>) {
        var session = ""
        for (header in resp.headers()){
            if (header.first=="Set-Cookie" && header.second.startsWith("session=")) {
                session = header.second.substringAfter("session=").split(';')[0]
                break
            }
        }
        if (session=="") throw ServerErrorException("/login", NO_SESSION_COOKIE, null)
        sessionCookie = session
    }
}