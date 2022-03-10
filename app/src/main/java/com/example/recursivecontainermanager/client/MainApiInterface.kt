package com.example.recursivecontainermanager.client

import com.example.recursivecontainermanager.data.entities.Item
import com.example.recursivecontainermanager.data.entities.Token
import com.example.recursivecontainermanager.data.entities.Tree
import com.example.recursivecontainermanager.data.entities.UserCredentials
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface MainApiInterface {

    //Manage users
    @GET("login")
    suspend fun authenticate(@Body credentials: UserCredentials): okhttp3.Response

    @POST("user")
    suspend fun createAccount(@Body credentials: UserCredentials)

    @DELETE("user/{userUuid}")
    suspend fun deleteAccount(@Path("userUuid") userUuid: String)


    //Manage Trees
    @GET("find")
    suspend fun findTree(@Query("item") item: String)

    @GET("tree/{treeUuid}")
    suspend fun getTree(
        @Path("treeUuid") treeUuid: String,
        @Header("If-None-Match") eTag: String
    ): Tree


    //Manage Items
    @POST("item")
    suspend fun addItem(@Body item: Item, @Header("If-Match") eTag: String)

    @PUT("item/{itemUuid}")
    suspend fun alterItem(
        @Path("itemUuid") itemUuid: String,
        @Body item: Item,
        @Header("If-Match") eTag: String
    )

    @DELETE("item/{itemUuid}")
    suspend fun deleteItem(@Path("itemUuid") itemUuid: String, @Header("If-Match") eTag: String)


    //Manage Tokens
    @POST("token")
    suspend fun createToken(@Body token: Token)

    @GET("token/{tokenCode}")
    suspend fun getToken(@Path("tokenCode") tokenCode: String): Tree
}