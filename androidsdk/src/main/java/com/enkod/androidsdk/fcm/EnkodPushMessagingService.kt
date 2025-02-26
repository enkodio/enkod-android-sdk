package com.enkod.androidsdk.fcm

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.enkod.androidsdk.common.EnKodSDK
import com.enkod.androidsdk.common.EnKodSDK.creatureInputDataFromMessage
import com.enkod.androidsdk.common.EnKodSDK.isAppInforegrounded
import com.enkod.androidsdk.common.EnKodSDK.logInfo
import com.enkod.androidsdk.common.EnKodSDK.managingTheNotificationCreationProcess
import com.enkod.androidsdk.common.InternetService
import com.enkod.androidsdk.common.LoadImageWorker
import com.enkod.androidsdk.utils.Preferences.MESSAGEID_TAG
import com.enkod.androidsdk.utils.Preferences.START_AUTO_UPDATE_TAG
import com.enkod.androidsdk.utils.Preferences.TAG
import com.enkod.androidsdk.utils.Preferences.USING_FCM
import com.enkod.androidsdk.utils.Variables.messageId
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

        val preferences = applicationContext.getSharedPreferences(TAG, Context.MODE_PRIVATE)

        val preferencesUsingFcm: Boolean? =
            preferences.getBoolean(USING_FCM, false)

        val preferencesTokenAutoUpdate: Boolean? =
        preferences.getBoolean (START_AUTO_UPDATE_TAG, false)

        if (preferencesUsingFcm == true) {

            logInfo("message.priority ${message.priority}")

            val dataFromPush =
                creatureInputDataFromMessage(message).keyValueMap as Map<String, String>

            val constraint =
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

            fun showPushWorkManager() {

                if (Build.VERSION.SDK_INT >= 31) {

                    logInfo("show push with expedition work manager api level >= 31")

                    val workRequest = OneTimeWorkRequestBuilder<LoadImageWorker>()

                        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                        .setInputData(creatureInputDataFromMessage(message))
                        .build()

                    WorkManager

                        .getInstance(applicationContext)
                        .enqueue(workRequest)

                } else if (Build.VERSION.SDK_INT < 31) {

                    logInfo("show push with work manager api level < 31")

                    val workRequest = OneTimeWorkRequestBuilder<LoadImageWorker>()

                        .setConstraints(constraint)
                        .setInputData(creatureInputDataFromMessage(message))
                        .build()

                    WorkManager

                        .getInstance(applicationContext)
                        .enqueue(workRequest)

                }
            }

            fun choosingNotificationProcessTopApi () {
                when (message.priority) {

                    1 -> {

                        managingTheNotificationCreationProcess(
                        applicationContext,
                        dataFromPush )

                        if (preferencesTokenAutoUpdate == true) {
                            TokenAutoUpdate.tokenUpdate(applicationContext)
                        }


                    }

                    2 -> showPushWorkManager()
                    else -> showPushWorkManager()

                }
            }


            if (!isAppInforegrounded()) {

                if (!message.data["image"].isNullOrEmpty()) {

                    if (Build.VERSION.SDK_INT < 31) {

                        val service = Intent(this, InternetService::class.java)

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                            this.startForegroundService(service)

                            choosingNotificationProcessTopApi()

                        } else {

                            when (message.priority) {

                                1 -> {

                                    managingTheNotificationCreationProcess(
                                        applicationContext,
                                        dataFromPush )

                                    if (preferencesTokenAutoUpdate == true) {
                                        TokenAutoUpdate.tokenUpdate(applicationContext)
                                    }

                                }

                                2 -> {

                                    this.startService(service)

                                    managingTheNotificationCreationProcess (
                                        applicationContext,
                                        dataFromPush
                                    )

                                    if (preferencesTokenAutoUpdate == true) {
                                        TokenAutoUpdate.tokenUpdate(applicationContext)
                                    }

                                }

                                else -> {

                                    this.startService(service)

                                    managingTheNotificationCreationProcess(
                                        applicationContext,
                                        dataFromPush
                                    )

                                    if (preferencesTokenAutoUpdate == true) {
                                        TokenAutoUpdate.tokenUpdate(applicationContext)
                                    }
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

                if (preferencesTokenAutoUpdate == true) {
                    TokenAutoUpdate.tokenUpdate(applicationContext)
                }
            }


            preferences.edit()
                .remove(MESSAGEID_TAG).apply()

            preferences.edit()
                .putString(MESSAGEID_TAG, "${dataFromPush[messageId]}")
                .apply()

        } else return
    }
}














