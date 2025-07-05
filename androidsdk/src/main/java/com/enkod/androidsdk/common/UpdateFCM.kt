package com.enkod.androidsdk.common

import android.content.Context
import com.enkod.androidsdk.common.EnKodSDK.logInfo
import com.enkod.androidsdk.utils.Preferences
import com.enkod.androidsdk.utils.Preferences.TAG
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

internal object UpdateFCM {

    private var fcm_token = ""

    internal fun updateFCMToken(context: Context, onTokenUpdated: (String) -> Unit = {}) {

        val preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE)
        var preferencesAcc = preferences.getString(Preferences.ACCOUNT_TAG, null)

        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(
                OnCompleteListener { newToken ->
                    if (!newToken.isSuccessful) {
                        logInfo("update fcm token failed")
                        return@OnCompleteListener
                    }
                    val token = newToken.result
                    EnKodSDK.fcmToken = token
                    //EnKodSDK.init(context = context, account = preferencesAcc ?: "", fcmToken = token)
                    logInfo("update fcm token success")
                    onTokenUpdated(token)
                })
        } catch (e: Exception) {
            logInfo("error fcm update token: $e")
        }
    }

}