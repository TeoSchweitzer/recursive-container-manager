package com.example.recursivecontainermanager.client

import com.example.recursivecontainermanager.data.entities.*
import retrofit2.Response
import retrofit2.http.*

interface MainApiInterface {

    //Manage users
    @POST("login")
    @FormUrlEncoded
    suspend fun authenticate(@Field("username") username: String,
                             @Field("password") password: String): Response<Unit>

    @POST("user")
    suspend fun createAccount(@Body credentials: User): Response<Unit>

    @DELETE("user/{username}")
    suspend fun deleteAccount(@Path("username") username: String): Response<Unit>


    //Manage Trees
    //@GET("find")
    //suspend fun findTree(@Query("item") item: String): Response<Unit>

    @GET("user/{username}")
    suspend fun getUserItems(
        @Path("username") username: String,
        @Header("If-None-Match") eTag: String
    ): Response<Tree>


    //Manage Items
    @POST("item")
    suspend fun addItem(@Body item: Item, @Header("If-Match") eTag: String): Response<Unit>

    @PUT("item/{itemUuid}")
    suspend fun alterItem(
        @Path("itemUuid") itemUuid: String,
        @Body item: Item,
        @Header("If-Match") eTag: String
    ): Response<Unit>

    @DELETE("item/{itemUuid}")
    suspend fun deleteItem(@Path("itemUuid") itemUuid: String, @Header("If-Match") eTag: String): Response<Unit>


    //Manage Tokens
    @POST("token")
    suspend fun createToken(@Body token: NewToken): Response<Unit>

    @GET("token/{tokenCode}")
    suspend fun getToken(@Path("tokenCode") tokenCode: String): Response<Unit>
}