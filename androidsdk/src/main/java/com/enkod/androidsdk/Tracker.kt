package com.enkod.androidsdk

data class Product(
    var id: String?,
    var categoryId: String? = null,
    var count: Int? = null,
    var price: String? = null,
    var picture: String? = null,
    var params: Map<String, Any>? = null
)

data class Order(

    var id: String?,
    var count: Int?,
    var price: String?,
    var params: Map<String, Any>? = null

)

data class PageUrl(
    val url: String
)

data class SessionIdResponse(
    var isActive: Boolean = false,
    var scriptSettings: Any? = null,
    var session_id: String = ""
)

data class UpdateTokenResponse(
    var sessionId: String = "",
    var token: String = ""
)

data class GetTokenResponse(
    var token: String = "",
    var sessionId: String = "",
    var os: String = ""
)

data class PushClickBody(
    val sessionId: String,
    val personId: Int,
    val messageId: Int,
    val intent: Int,
    val url: String?
)

data class SubscribeBody(
    val sessionId: String,
    val token: String,
    val os: String? = null
)