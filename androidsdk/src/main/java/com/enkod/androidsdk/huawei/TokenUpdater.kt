package com.enkod.androidsdk.huawei

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

object TokenUpdater {
    private val _tokenUpdateChannel = Channel<String?>(Channel.BUFFERED)
    val tokenUpdateChannel = _tokenUpdateChannel.receiveAsFlow()

    fun setNewToken(token: String?) : Boolean =
        _tokenUpdateChannel.trySend(token).isSuccess
}