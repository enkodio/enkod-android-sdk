package com.enkod.androidsdk.huawei

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.enkod.androidsdk.common.EnKodSDK
import com.enkod.androidsdk.common.EnKodSDK.createInputDataFromHuaweiMessage
import com.enkod.androidsdk.common.EnKodSDK.isAppInforegrounded
import com.enkod.androidsdk.common.EnKodSDK.logInfo
import com.enkod.androidsdk.common.EnKodSDK.managingTheNotificationCreationProcess
import com.enkod.androidsdk.common.InternetService
import com.enkod.androidsdk.common.LoadImageWorker
import com.enkod.androidsdk.fcm.TokenAutoUpdate
import com.enkod.androidsdk.utils.Preferences.MESSAGEID_TAG
import com.enkod.androidsdk.utils.Preferences.START_AUTO_UPDATE_TAG
import com.enkod.androidsdk.utils.Preferences.TAG
import com.enkod.androidsdk.utils.Preferences.USING_FCM
import com.enkod.androidsdk.utils.Preferences.USING_HUAWEI
import com.enkod.androidsdk.utils.Variables.messageId
import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage

class HuaweiPushService : HmsMessageService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        logInfo("new token is $token")

        TokenUpdater.setNewToken(token).let {
            logInfo("\"token updating status is $it\"")
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        logInfo("onMessageReceived method started")
        super.onMessageReceived(message)

        val preferences = applicationContext.getSharedPreferences(TAG, Context.MODE_PRIVATE)

        val preferencesUsingHuawei: Boolean =
            preferences.getBoolean(USING_HUAWEI, false)

        if (preferencesUsingHuawei) {

            logInfo("message.priority ${message.dataOfMap["priority"]}")

            val dataFromPush =
                createInputDataFromHuaweiMessage(message).keyValueMap as Map<String, String>

            val constraint =
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

            fun showPushWorkManager() {

                if (Build.VERSION.SDK_INT >= 31) {

                    logInfo("show push with expedition work manager api level >= 31")

                    val workRequest = OneTimeWorkRequestBuilder<LoadImageWorker>()

                        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                        .setInputData(createInputDataFromHuaweiMessage(message))
                        .build()

                    WorkManager

                        .getInstance(applicationContext)
                        .enqueue(workRequest)

                } else if (Build.VERSION.SDK_INT < 31) {

                    logInfo("show push with work manager api level < 31")

                    val workRequest = OneTimeWorkRequestBuilder<LoadImageWorker>()

                        .setConstraints(constraint)
                        .setInputData(createInputDataFromHuaweiMessage(message))
                        .build()

                    WorkManager

                        .getInstance(applicationContext)
                        .enqueue(workRequest)

                }
            }

            fun choosingNotificationProcessTopApi() {
                when ((message.dataOfMap["priority"] as String).toInt()) {

                    1 -> {

                        managingTheNotificationCreationProcess(
                            applicationContext,
                            dataFromPush
                        )
                    }

                    2 -> showPushWorkManager()
                    else -> showPushWorkManager()

                }
            }


            if (!isAppInforegrounded()) {

                if (!message.dataOfMap["image"].isNullOrEmpty()) {

                    if (Build.VERSION.SDK_INT < 31) {

                        val service = Intent(this, InternetService::class.java)

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                            this.startForegroundService(service)

                            choosingNotificationProcessTopApi()

                        } else {

                            when ((message.dataOfMap["priority"] as String).toInt()) {

                                1 -> {

                                    managingTheNotificationCreationProcess(
                                        applicationContext,
                                        dataFromPush
                                    )
                                }

                                2 -> {

                                    this.startService(service)

                                    managingTheNotificationCreationProcess(
                                        applicationContext,
                                        dataFromPush
                                    )
                                }

                                else -> {

                                    this.startService(service)

                                    managingTheNotificationCreationProcess(
                                        applicationContext,
                                        dataFromPush
                                    )
                                }
                            }
                        }

                    } else if (Build.VERSION.SDK_INT >= 31) {

                        choosingNotificationProcessTopApi()

                    }

                } else {
                    managingTheNotificationCreationProcess(applicationContext, dataFromPush)
                }

            } else {
                managingTheNotificationCreationProcess(applicationContext, dataFromPush)
            }

            preferences.edit()
                .remove(MESSAGEID_TAG).apply()

            preferences.edit()
                .putString(MESSAGEID_TAG, "${dataFromPush[messageId]}")
                .apply()

        } else return
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
        EnKodSDK.onDeletedMessage()
    }
}