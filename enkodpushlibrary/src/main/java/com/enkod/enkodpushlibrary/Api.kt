package com.enkod.enkodpushlibrary

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface Api {
    @POST("/sessions/")
    fun getSessionId(@Header("X-Account")client:String): Call<SessionIdResponse>

    @POST("sessions/start")
    fun startSession(@Header("X-Session-Id")sessionID: String, @Header("X-Account")client:String): Call<SessionIdResponse>

    @PUT("mobile/token")
    @Headers("Content-Type: application/json")
    fun updateToken(
        @Header("X-Account")client:String,
        @Header("X-Session-Id") session:String,
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
        @Header("X-Account")client:String,
        @Header("X-Session-Id") session:String,
        @Body subscribeBody: SubscribeBody
    ): Call<UpdateTokenResponse>


    @POST("mobile/click")
    @Headers("Content-Type: application/json")
    fun pushClick(
        @Header("X-Account")client:String,
        @Body pushClickBody: PushClickBody
    ): Call<PushClickBody>


    @POST("/subscribe")
    fun subscribe(@Header("X-Account")client:String,
                  @Header("X-Session-Id")session:String,
                  @Body subscribeBody:JsonObject)
    : Call<Unit>

    @PUT("updateBySession")
    fun updateContacts(@Header("X-Account") client: String,
                       @Header("X-Session-Id") session: String,
                       @QueryMap(encoded = true) params: Map<String, String>):Call<Unit>


    @POST("/mobile/product/cart")
    fun addToCart(@Header("X-Account")client:String,
                  @Header("X-Session-Id")session:String,
                  @Body body:JsonObject)
            : Call<Unit>


    @POST("/mobile/product/favourite")
    fun addToFavourite(@Header("X-Account")client:String,
                  @Header("X-Session-Id")session:String,
                  @Body body:JsonObject)
    : Call<Unit>


    @POST("/mobile/page/open")
    fun pageOpen(@Header("X-Account")client:String,
                       @Header("X-Session-Id")session:String,
                       @Body body:PageUrl)
    : Call<Unit>

    @POST("/mobile/product/open")
    fun productOpen(@Header("X-Account")client:String,
                 @Header("X-Session-Id")session:String,
                 @Body body:JsonObject)
            : Call<Unit>

    @POST("/mobile/product/order")
    fun order(@Header("X-Account")client:String,
                    @Header("X-Session-Id")session:String,
                    @Body body:JsonObject)
            : Call<Unit>


}



