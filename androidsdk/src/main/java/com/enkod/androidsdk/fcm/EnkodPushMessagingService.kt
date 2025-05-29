package com.enkod.androidsdk.fcm

import android.annotation.SuppressLint
import com.enkod.androidsdk.common.EnKodSDK
import com.enkod.androidsdk.common.EnKodSDK.logInfo
import com.enkod.androidsdk.common.EnKodSDK.manageFcmMessageWithEnKod
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


//класс EnkodPushMessagingService расширяет FirebaseMessagingService() необходим для получения push уведомлений через fcm.
class EnkodPushMessagingService : FirebaseMessagingService() {


    override fun onNewToken(token: String) {
        super.onNewToken(token)

        logInfo("new token $token")

    }

    override fun onDeletedMessages() {

        EnKodSDK.onDeletedMessage()
    }


    @SuppressLint("SwitchIntDef")
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        manageFcmMessageWithEnKod(
            message,
            applicationContext,
            false,
        )
    }
}














