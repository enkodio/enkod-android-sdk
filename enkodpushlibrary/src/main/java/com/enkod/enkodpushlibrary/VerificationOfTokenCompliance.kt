package com.enkod.enkodpushlibrary

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.enkod.enkodpushlibrary.Variables.defaultTimeVerificationToken
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

internal object VerificationOfTokenCompliance {

    
    internal fun startVerificationTokenUsingWorkManager (context: Context) {

        EnkodPushLibrary.logInfo("start verification function using workManager")

        val constraint =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        val workRequest =

            PeriodicWorkRequestBuilder<verificationOfTokenWorkManager>(
                defaultTimeVerificationToken.toLong(),
                TimeUnit.HOURS
            )
                .setInitialDelay(1, TimeUnit.MINUTES)
                .setConstraints(constraint)
                .build()

        WorkManager

            .getInstance(context)
            .enqueueUniquePeriodicWork(
                "verificationOfTokenWorker",
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )

    }


    class verificationOfTokenWorkManager(
        context: Context,
        workerParameters: WorkerParameters
    ) :

        Worker(context, workerParameters) {


        @RequiresApi(Build.VERSION_CODES.O)
        override fun doWork(): Result {


            EnkodPushLibrary.logInfo("verification in process using workManager")

            preparationVerification(applicationContext)

            return Result.success()

        }
    }




    fun preparationVerification (context: Context) {

        EnkodPushLibrary.initPreferences(context)
        EnkodPushLibrary.initRetrofit(context)

        val preferences =
            context.getSharedPreferences(Preferences.TAG, Context.MODE_PRIVATE)
        val preferencesAcc = preferences.getString(Preferences.ACCOUNT_TAG, null)
        val preferencesSession = preferences.getString(Preferences.SESSION_ID_TAG, null)


        if (preferencesAcc != null && preferencesSession != null) {

            try {

                FirebaseMessaging.getInstance().token.addOnCompleteListener(
                    OnCompleteListener { task ->

                        if (!task.isSuccessful) {

                            return@OnCompleteListener
                        }

                        val currentToken = task.result

                        verificationOfTokenCompliance(
                            context,
                            preferencesAcc,
                            preferencesSession,
                            currentToken
                        )

                    })

            } catch (e: Exception) {

                EnkodPushLibrary.logInfo("error in verification preparation: $e")

            }
        }
    }


    internal fun verificationOfTokenCompliance(
        context: Context,
        account: String?,
        session: String?,
        currentToken: String?

    ) {

        val account = account ?: ""
        val session = session ?: ""

        EnkodPushLibrary.retrofit.getToken(
            account,
            session
        ).enqueue(object : Callback<GetTokenResponse> {

            override fun onResponse(
                call: Call<GetTokenResponse>,
                response: Response<GetTokenResponse>
            ) {

                val body = response.body()
                var tokenOnService = ""

                when (body) {

                    null -> return
                    else -> {

                        tokenOnService = body.token


                        if (tokenOnService == currentToken) {


                            WorkManager.getInstance(context)
                                .cancelUniqueWork("verificationOfTokenWorker")


                            EnkodPushLibrary.logInfo("token verification true")

                        } else {

                            EnkodPushLibrary.init(context, account, currentToken)

                            EnkodPushLibrary.logInfo("token verification false reload Enkod library")

                        }
                    }
                }
            }

            override fun onFailure(call: Call<GetTokenResponse>, t: Throwable) {

                EnkodPushLibrary.logInfo("token verification error retrofit $t")

                return

            }
        })
    }
}


