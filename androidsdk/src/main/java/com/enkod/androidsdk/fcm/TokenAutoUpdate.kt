package com.enkod.androidsdk.fcm


import android.content.Context

import com.enkod.androidsdk.common.EnKodSDK.initPreferences
import com.enkod.androidsdk.common.EnKodSDK.initRetrofit
import com.enkod.androidsdk.common.EnKodSDK.logInfo
import com.enkod.androidsdk.common.EnKodSDK.startTokenAutoUpdateObserver
import com.enkod.androidsdk.utils.Preferences.TAG
import com.enkod.androidsdk.utils.Variables.defaultTimeAutoUpdateToken
import com.enkod.androidsdk.utils.Variables.millisInHours
import com.enkod.androidsdk.fcm.VerificationOfTokenCompliance.startVerificationTokenUsingWorkManager
import com.enkod.androidsdk.common.EnKodSDK
import com.enkod.androidsdk.utils.Preferences
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

// объект TokenAutoUpdate - предназначен для обновления токена
// процесс обновления просходит в момент получения высокоприоритетных push уведомления,
// за исключением уведомлений среднего приоритета на устройствах Build.VERSION.SDK_INT >= 31.

internal object TokenAutoUpdate {

    internal fun tokenUpdate(context: Context) {

        startTokenAutoUpdateObserver.value = true

        initPreferences(context)
        initRetrofit(context)

        val preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE)

        var preferencesAcc = preferences.getString(Preferences.ACCOUNT_TAG, null)

        val timeLastTokenUpdate: Long? =
            preferences.getLong(Preferences.TIME_LAST_TOKEN_UPDATE_TAG, 0)

        val setTimeTokenUpdate: Int? =
            preferences.getInt(Preferences.TIME_TOKEN_AUTO_UPDATE_TAG, defaultTimeAutoUpdateToken)

        val timeAutoUpdate =
            (setTimeTokenUpdate ?: defaultTimeAutoUpdateToken) * millisInHours


        if (preferencesAcc != null) {

            if (timeLastTokenUpdate != null && timeLastTokenUpdate > 0) {

                if ((System.currentTimeMillis() - timeLastTokenUpdate) >= timeAutoUpdate) {

                    logInfo("token auto update function")

                    updateProcess(context, preferencesAcc)

                } else {

                    return
                }
            }
        }
    }

    fun updateProcess(context: Context, preferencesAcc: String) {

        try {
            FirebaseMessaging.getInstance().deleteToken()

                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        logInfo("token auto update: delete old token")

                        FirebaseMessaging.getInstance().token.addOnCompleteListener(

                            OnCompleteListener { newToken ->

                                if (!newToken.isSuccessful) {

                                    startVerificationTokenUsingWorkManager(context)

                                    logInfo("error get new token in token auto update function")

                                    return@OnCompleteListener
                                }

                                val token = newToken.result

                                EnKodSDK.init(
                                    context,
                                    preferencesAcc,
                                    token
                                )

                                logInfo("token update in auto update function")

                                startVerificationTokenUsingWorkManager(context)

                            })

                    } else {

                        startVerificationTokenUsingWorkManager(context)

                        logInfo("error deletion token in token auto update function")

                    }
                }

        } catch (e: Exception) {

            startVerificationTokenUsingWorkManager(context)

            logInfo("error in  token auto update function: $e")

        }
    }
}
