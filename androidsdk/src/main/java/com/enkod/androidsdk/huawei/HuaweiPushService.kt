package com.enkod.androidsdk.huawei

import android.content.Context
import com.enkod.androidsdk.common.EnKodSDK
import com.enkod.androidsdk.common.EnKodSDK.logInfo
import com.enkod.androidsdk.common.EnKodSDK.processHuaweiMessageWithSdk
import com.enkod.androidsdk.utils.Preferences.TAG
import com.enkod.androidsdk.utils.Preferences.USING_HUAWEI
import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage

class HuaweiPushService : HmsMessageService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        logInfo("new token is $token")

        logInfo("\"token updating status is ${TokenUpdater.setNewToken(token)}\"")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        logInfo("onMessageReceived method started")
        super.onMessageReceived(message)

        val preferences = applicationContext.getSharedPreferences(TAG, Context.MODE_PRIVATE)

        val preferencesUsingHuawei: Boolean =
            preferences.getBoolean(USING_HUAWEI, false)

        if (preferencesUsingHuawei)
            processHuaweiMessageWithSdk(
                message = message,
                applicationContext = applicationContext,
            ) else return
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
        EnKodSDK.onDeletedMessage()
    }
}