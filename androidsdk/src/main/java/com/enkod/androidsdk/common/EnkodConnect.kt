package com.enkod.androidsdk.common

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.edit
import com.enkod.androidsdk.common.EnKodSDK.initSecondaryFirebaseApp
import com.enkod.androidsdk.common.EnKodSDK.isAppInforegrounded
import com.enkod.androidsdk.common.EnKodSDK.logInfo
import com.enkod.androidsdk.common.EnKodSDK.startTokenManualUpdateObserver
import com.enkod.androidsdk.fcm.EnkodPushMessagingService
import com.enkod.androidsdk.fcm.TokenManualUpdateService
import com.enkod.androidsdk.huawei.TokenUpdater
import com.enkod.androidsdk.utils.Preferences.START_AUTO_UPDATE_TAG
import com.enkod.androidsdk.utils.Preferences.TAG
import com.enkod.androidsdk.utils.Preferences.TIME_LAST_TOKEN_UPDATE_TAG
import com.enkod.androidsdk.utils.Preferences.TIME_TOKEN_AUTO_UPDATE_TAG
import com.enkod.androidsdk.utils.Preferences.USING_FCM
import com.enkod.androidsdk.utils.Preferences.USING_HUAWEI
import com.enkod.androidsdk.utils.Variables.defaultTimeAutoUpdateToken
import com.enkod.androidsdk.utils.Variables.defaultTimeManualUpdateToken
import com.enkod.androidsdk.utils.Variables.millisInHours
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.huawei.agconnect.config.AGConnectServicesConfig
import com.huawei.hms.aaid.HmsInstanceId
import com.huawei.hms.common.ApiException
import com.huawei.hms.push.HmsMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// класс EnkodConnect предназначен для активации библиотеки содержит конструктор,
// в который входят поля для настройки библиотеки.

class EnkodConnect(

    _account: String?,
    _usingFcm: Boolean? = false,
    _usingHuawei: Boolean? = false,
    _tokenManualUpdate: Boolean? = true,
    _tokenAutoUpdate: Boolean? = true,
    _timeTokenManualUpdate: Int? = null,
    _timeTokenAutoUpdate: Int? = null,
    _usingInternalNotificationsService: Boolean = false,
    _secondFirebaseJsonName: String? = null,
) {

    private val account: String
    private val usingFcm: Boolean
    private val usingHuawei: Boolean
    private val tokenManualUpdate: Boolean
    private val tokenAutoUpdate: Boolean
    private var timeTokenManualUpdate: Int
    private var timeTokenAutoUpdate: Int
    private var usingInternalNotificationService: Boolean
    private var secondFirebaseJsonName: String


    init {

        account = _account ?: ""
        usingFcm = _usingFcm ?: false
        usingHuawei = _usingHuawei ?: false
        tokenManualUpdate = _tokenManualUpdate ?: true
        tokenAutoUpdate = _tokenAutoUpdate ?: true
        usingInternalNotificationService = _usingInternalNotificationsService
        secondFirebaseJsonName = _secondFirebaseJsonName ?: ""

        timeTokenManualUpdate =

            if (_timeTokenManualUpdate != null && _timeTokenManualUpdate > 0) _timeTokenManualUpdate
            else defaultTimeManualUpdateToken

        timeTokenAutoUpdate =

            if (_timeTokenAutoUpdate != null && _timeTokenAutoUpdate > 0) _timeTokenAutoUpdate
            else defaultTimeAutoUpdateToken
    }


    fun start(context: Context) {

        logInfo("user settings: account: $account, fcm: $usingFcm, huawei: $usingHuawei, tokenMU: $tokenManualUpdate, tokenAU: $tokenAutoUpdate, timeMU: $timeTokenManualUpdate, timeAU: $timeTokenAutoUpdate, usingInternalNotificationService: $usingInternalNotificationService")

        val preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE)

        if (usingInternalNotificationService) {
            val componentName = ComponentName(context, EnkodPushMessagingService::class.java)
            context.packageManager.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
        }

        CoroutineScope(Dispatchers.IO).launch {
            TokenUpdater.tokenUpdateChannel.collectLatest { newToken ->
                logInfo("Received new token: $newToken")

                EnKodSDK.init(context, EnKodSDK.getAccount()!!, null, newToken)
            }
        }

        when (usingFcm) {

            true -> {

                if (usingInternalNotificationService) {
                    preferences.edit() {
                        putBoolean(USING_FCM, true)
                    }

                    if (EnKodSDK.isOnline(context)) {

                        EnKodSDK.isOnlineStatus(true)

                        try {

                            FirebaseMessaging.getInstance().token.addOnCompleteListener(

                                OnCompleteListener { task ->

                                    if (!task.isSuccessful) {

                                        return@OnCompleteListener
                                    }

                                    val token = task.result

                                    logInfo("current fcm token: $token")

                                    if (tokenAutoUpdate) {

                                        preferences.edit() {

                                            putBoolean(START_AUTO_UPDATE_TAG, true)
                                        }

                                        preferences.edit() {

                                            putInt(
                                                TIME_TOKEN_AUTO_UPDATE_TAG,
                                                timeTokenAutoUpdate
                                            )
                                        }

                                    }

                                    if (tokenManualUpdate) {

                                        startTokenManualUpdateObserver.value = false

                                        tokenUpdate(context, timeTokenManualUpdate)

                                    }

                                    EnKodSDK.init(context, account, token)

                                    logInfo("start library with fcm")

                                })

                        } catch (e: Exception) {

                            EnKodSDK.init(context, account)

                            logInfo("the library started using fcm with an error")
                        }

                    } else {

                        EnKodSDK.isOnlineStatus(false)

                        logInfo("error internet")
                    }
                }
            }

            false -> {

                preferences.edit()

                    .putBoolean(USING_FCM, false)
                    .apply()
                when (usingHuawei) {

                    true -> {

                        preferences.edit()

                            .putBoolean(USING_HUAWEI, true)
                            .apply()

                        if (EnKodSDK.isOnline(context)) {

                            EnKodSDK.isOnlineStatus(true)

                            try {

                                HmsInstanceId.getInstance(context).run {

                                    //Получаем id AppGallery приложения из конфига
                                    val appId = AGConnectServicesConfig.fromContext(context)
                                        .getString("client/app_id")

                                    object : Thread() {
                                        override fun run() {
                                            try {
                                                //Получаем HMS пуш-токен по id, который достали ранее
                                                val pushToken =
                                                    getToken(
                                                        appId,
                                                        HmsMessaging.DEFAULT_TOKEN_SCOPE
                                                    )

                                                if (!pushToken.isNullOrBlank()) {
                                                    logInfo("current huawei token: $pushToken")

                                                    EnKodSDK.init(context, account, null, pushToken)

                                                    logInfo("start library with huawei")
                                                }
                                            } catch (e: ApiException) {
                                                e.message?.let { Log.e("HuaweiError", it) }
                                            }
                                        }

                                    }.start()
                                }

                            } catch (e: Exception) {

                                EnKodSDK.init(context, account)

                                logInfo("the library started using huawei with an error")

                            }

                        } else {

                            EnKodSDK.isOnlineStatus(false)

                            logInfo("error internet")
                        }
                    }

                    false -> {

                        preferences.edit()

                            .putBoolean(USING_HUAWEI, false)
                            .apply()

                        if (EnKodSDK.isOnline(context)) {
                            EnKodSDK.isOnlineStatus(true)
                            EnKodSDK.init(context, account)
                            logInfo("start library without huawei and fcm")
                        } else {

                            EnKodSDK.isOnlineStatus(false)
                            logInfo("error internet")
                        }
                    }
                }
            }
        }
    }

    private fun tokenUpdate(context: Context, timeUpdate: Int) {

        val timeUpdateInMillis: Long = ((timeUpdate * millisInHours)).toLong()

        val preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE)
        val timeLastTokenUpdate = preferences.getLong(TIME_LAST_TOKEN_UPDATE_TAG, 0)

        if (isAppInforegrounded()) {

            if (EnKodSDK.isOnline(context)) {

                if (timeLastTokenUpdate > 0) {

                    if ((System.currentTimeMillis() - timeLastTokenUpdate) >= timeUpdateInMillis) {

                        logInfo("start manual update in start method")

                        startTokenManualUpdateObserver.value = true

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(
                                Intent(
                                    context,
                                    TokenManualUpdateService::class.java
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

