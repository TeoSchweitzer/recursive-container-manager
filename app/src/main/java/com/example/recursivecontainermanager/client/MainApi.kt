package com.example.recursivecontainermanager.client

import com.example.recursivecontainermanager.data.entities.UserCredentials
import com.example.recursivecontainermanager.exceptions.ServerError.*
import com.example.recursivecontainermanager.exceptions.ServerErrorException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory



/**
 * The base URL will be added by the interceptor
 */
private const val BASE_URL = ""

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

/**
 * Add Interceptor
 */
private val client = OkHttpClient().newBuilder().addInterceptor(SessionInterceptor()).build()


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
    private var serverAddress = "/"
    private var sessionCookie = ""

    fun getSessionCookie() = sessionCookie

    fun getServerAddress() = serverAddress
    fun setServerAddress(address: String) {
        serverAddress = if (address.endsWith("/")) address else "$address/"
    }

    suspend fun authenticate(name: String, password: String): String {
        val response = retrofitService.authenticate(UserCredentials(name, password))
        if (!response.isRedirect) throw ServerErrorException(response.request.url, IMPOSSIBLE_STATUS_RESPONSE, response.code.toString())
        setNewSession(response)
        return response.headers["Location"]!!
    }


    private fun setNewSession(resp: Response) {
        var session = ""
        for (header in resp.headers){
            if (header.first=="Set-Cookie" && header.second.startsWith("session=")) {
                session = header.second.substringAfter("session=").split(';')[0]
                break
            }
        }
        if (session=="") throw ServerErrorException(resp.request.url, NO_SESSION_COOKIE, null)
        sessionCookie = session
    }
}