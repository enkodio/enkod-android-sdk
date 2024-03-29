package com.enkod.androidsdk

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import com.enkod.androidsdk.EnKodSDK.createdNotificationForService
import com.enkod.androidsdk.EnKodSDK.logInfo
import com.enkod.androidsdk.Variables.defaultImageLoadTimeout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class InternetService : Service() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {

        logInfo("serviceCreated")


        super.onCreate()

        Thread.setDefaultUncaughtExceptionHandler { paramThread, paramThrowable ->

            exitProcess(0)

        }

        var startAutoUpdateToken = false

        EnKodSDK.startTokenAutoUpdateObserver.observable.subscribe { start ->

            if (start) {

                startAutoUpdateToken = start
            }
        }


        EnKodSDK.pushLoadObserver.observable.subscribe { completed ->

            if (completed) {


                when (startAutoUpdateToken) {

                    false -> {
                        logInfo("stopSelf")

                        stopSelf()
                        exitProcess(0)
                    }

                    else -> {

                        EnKodSDK.initLibObserver.observable.subscribe { update ->

                            if (update) {

                                logInfo("stopSelf + tokenUpdate")
                                stopSelf()
                                exitProcess(0)

                            }
                        }
                    }
                }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {

            delay(3400)

           logInfo("startSelf")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (Build.VERSION.SDK_INT >= 34) {

                    logInfo("startSelf api >= 34")
                    startForeground(
                        1,
                        createdNotificationForService(applicationContext),
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                    )
                }else {

                    logInfo("startSelf api < 34")
                    startForeground(1, createdNotificationForService(applicationContext))
                }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {

                   delay(defaultImageLoadTimeout.toLong())
                   stopSelf()
        }
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)

    }

}