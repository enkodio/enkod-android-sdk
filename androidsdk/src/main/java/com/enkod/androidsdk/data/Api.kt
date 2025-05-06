package com.enkod.androidsdk.data

import com.enkod.androidsdk.data.model.GetTokenResponse
import com.enkod.androidsdk.data.model.PageUrl
import com.enkod.androidsdk.data.model.PushClickBody
import com.enkod.androidsdk.data.model.SessionIdResponse
import com.enkod.androidsdk.data.model.SubscribeBody
import com.enkod.androidsdk.data.model.UpdateTokenResponse
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query
import retrofit2.http.QueryMap

// интерфейс Api содержит функции запросов для Retrofit.

interface Api {
    @POST("/sessions/")
    fun getSessionId(@Header("X-Account") client: String): Call<SessionIdResponse>

    @POST("sessions/start")
    fun startSession(
        @Header("X-Session-Id") sessionID: String,
        @Header("X-Account") client: String
    ): Call<SessionIdResponse>

    @PUT("mobile/token")
    @Headers("Content-Type: application/json")
    fun updateToken(
        @Header("X-Account") client: String,
        @Header("X-Session-Id") session: String,
        @Body subscribeBody: SubscribeBody,
    ): Call<UpdateTokenResponse>

    @GET("mobile/token")
    fun getToken(
        @Header("X-Account") client: String,
        @Query("session") session: String,
    ): Call<GetTokenResponse>

    @POST("mobile/subscribe")
    @Headers("Content-Type: application/json")
    fun subscribeToPushToken(
        @Header("X-Account") client: String,
        @Header("X-Session-Id") session: String,
        @Body subscribeBody: SubscribeBody
    ): Call<UpdateTokenResponse>


    @POST("mobile/click")
    @Headers("Content-Type: application/json")
    fun pushClick(
        @Header("X-Account") client: String,
        @Body pushClickBody: PushClickBody
    ): Call<PushClickBody>


    @POST("/subscribe")
    fun subscribe(
        @Header("X-Account") client: String,
        @Header("X-Session-Id") session: String,
        @Body subscribeBody: JsonObject
    )
            : Call<Unit>

    @PUT("updateBySession")
    fun updateContacts(
        @Header("X-Account") client: String,
        @Header("X-Session-Id") session: String,
        @QueryMap(encoded = true) params: Map<String, String>
    ): Call<Unit>


    @POST("/mobile/product/cart")
    fun addToCart(
        @Header("X-Account") client: String,
        @Header("X-Session-Id") session: String,
        @Body body: JsonObject
    )
            : Call<Unit>


    @POST("/mobile/product/favourite")
    fun addToFavourite(
        @Header("X-Account") client: String,
        @Header("X-Session-Id") session: String,
        @Body body: JsonObject
    )
            : Call<Unit>


    @POST("/mobile/page/open")
    fun pageOpen(
        @Header("X-Account") client: String,
        @Header("X-Session-Id") session: String,
        @Body body: PageUrl
    )
            : Call<Unit>

    @POST("/mobile/product/open")
    fun productOpen(
        @Header("X-Account") client: String,
        @Header("X-Session-Id") session: String,
        @Body body: JsonObject
    )
            : Call<Unit>

    @POST("/mobile/product/order")
    fun order(
        @Header("X-Account") client: String,
        @Header("X-Session-Id") session: String,
        @Body body: JsonObject
    )
            : Call<Unit>

    @POST("/event")
    @Headers("Content-Type: application/json")
    fun sendCustomEvent(
        @Header("X-Session-Id") sessionID: String,
        @Header("X-Account") client: String,
        @Body customEventMap: Map<String, @JvmSuppressWildcards Any>
    ): Call<Unit>
}