package com.enkod.enkodpushlibrary


import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.enkod.enkodpushlibrary.EnkodPushLibrary.initPreferences
import com.enkod.enkodpushlibrary.EnkodPushLibrary.initRetrofit
import com.enkod.enkodpushlibrary.EnkodPushLibrary.isAppInforegrounded
import com.enkod.enkodpushlibrary.EnkodPushLibrary.logInfo
import com.enkod.enkodpushlibrary.EnkodPushLibrary.startTokenAutoUpdateObserver
import com.enkod.enkodpushlibrary.Preferences.TAG
import com.enkod.enkodpushlibrary.Variables.defaultTimeAutoUpdateToken
import com.enkod.enkodpushlibrary.Variables.millisInHours
import com.enkod.enkodpushlibrary.VerificationOfTokenCompliance.startVerificationTokenUsingWorkManager
import com.google.firebase.messaging.FirebaseMessaging
import java.util.concurrent.TimeUnit


internal object TokenAutoUpdate {

    fun startTokenAutoUpdateUsingWorkManager (context: Context, time: Int) {

        logInfo("token auto update work start")

        val constraint =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        val workRequest =

            PeriodicWorkRequestBuilder<TokenAutoUpdateWorkManager>(
                time.toLong(),
                TimeUnit.HOURS
            )

                .setInitialDelay(time.toLong(),TimeUnit.HOURS)
                .setConstraints(constraint)
                .build()

        WorkManager

            .getInstance(context)
            .enqueueUniquePeriodicWork(
                "tokenAutoUpdateWorker",
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )

    }

    class TokenAutoUpdateWorkManager(
        context: Context,
        workerParameters: WorkerParameters
    ) :

        Worker(context, workerParameters) {


        override fun doWork(): Result {

            val preferences = applicationContext.getSharedPreferences(TAG, Context.MODE_PRIVATE)

            val preferencesUsingFcm: Boolean? =
                preferences.getBoolean(Preferences.USING_FCM, false)

            if (preferencesUsingFcm != null && preferencesUsingFcm == true) {

                if (isAppInforegrounded()) {

                    EnkodPushLibrary.startTokenManualUpdateObserver.observable.subscribe {start ->
                       when (start) {
                           true -> {
                               logInfo("auto update canceled manual update activated")
                               return@subscribe
                           }
                           false -> tokenUpdate(applicationContext)
                       }
                    }
                }
                else tokenUpdate(applicationContext)
            }

            return Result.success()

        }
    }

    private fun tokenUpdate(context: Context) {

        startTokenAutoUpdateObserver.value = true

        initPreferences(context)
        initRetrofit(context)

        logInfo( "token auto update function")

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

                if ((System.currentTimeMillis() - timeLastTokenUpdate) >= timeAutoUpdate-60000) {

                    try {

                        FirebaseMessaging.getInstance().deleteToken()

                            .addOnCompleteListener { task ->

                                if (task.isSuccessful) {

                                    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->

                                        if (task.isSuccessful) {

                                            val token = task.result

                                            EnkodPushLibrary.init(
                                                context,
                                                preferencesAcc,
                                                token
                                            )

                                                startVerificationTokenUsingWorkManager(context)


                                            logInfo( "token update in auto update function")

                                        } else {


                                                startVerificationTokenUsingWorkManager(context)


                                            logInfo("error get new token in token auto update function")

                                        }
                                    }

                                } else {


                                        startVerificationTokenUsingWorkManager(context)


                                    logInfo("error deletion token in token auto update function")

                                }
                            }

                    } catch (e: Exception) {


                            startVerificationTokenUsingWorkManager(context)


                        logInfo("error in  token auto update function: $e")

                    }
                } else {
                    return
                }
            }
        }
    }
}