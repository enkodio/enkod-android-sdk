package com.enkod.enkodpushlibrary

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import com.enkod.enkodpushlibrary.EnkodPushLibrary.logInfo
import com.enkod.enkodpushlibrary.Preferences.ACCOUNT_TAG
import com.enkod.enkodpushlibrary.Preferences.TAG
import com.enkod.enkodpushlibrary.VerificationOfTokenCompliance.startVerificationTokenUsingWorkManager
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TokenManualUpdateService : Service() {


    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                1,
                EnkodPushLibrary.createdNotificationForService(this),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForeground(1, EnkodPushLibrary.createdNotificationForService(this))
            }
        }

        CoroutineScope(Dispatchers.IO).launch {

            delay(3000)

            EnkodPushLibrary.initPreferences(applicationContext)
            EnkodPushLibrary.initRetrofit(applicationContext)

            val preferences = applicationContext.getSharedPreferences(TAG, Context.MODE_PRIVATE)

            var preferencesAcc = preferences.getString(ACCOUNT_TAG, null)

            if (preferencesAcc != null) {

                try {

                    FirebaseMessaging.getInstance().deleteToken()

                        .addOnCompleteListener { task ->

                            if (task.isSuccessful) {

                                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->

                                    if (task.isSuccessful) {

                                        val token = task.result

                                        EnkodPushLibrary.init(
                                            applicationContext,
                                            preferencesAcc,
                                            token
                                        )
                                        logInfo ("token manual update" )


                                            startVerificationTokenUsingWorkManager(applicationContext)


                                        CoroutineScope(Dispatchers.IO).launch {

                                            delay(5000)

                                            stopSelf()
                                        }

                                    } else {

                                            startVerificationTokenUsingWorkManager(applicationContext)

                                        logInfo("error get new token in UpdateTokenService")

                                        stopSelf()
                                    }
                                }

                            } else {

                                    startVerificationTokenUsingWorkManager(applicationContext)


                                logInfo("error deletion token in UpdateTokenService")

                                stopSelf()
                            }
                        }

                } catch (e: Exception) {

                        startVerificationTokenUsingWorkManager(applicationContext)

                    logInfo("error in UpdateTokenService $e")

                    stopSelf()

                }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {

            delay(15000)
            stopSelf()

        }
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}