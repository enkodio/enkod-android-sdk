package com.enkod.enkodpushlibrary

import android.app.NotificationChannel
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.enkod.enkodpushlibrary.EnkodPushLibrary.defaultIconId
import com.enkod.enkodpushlibrary.Variables.actionButtonIntent
import com.enkod.enkodpushlibrary.Variables.actionButtonText
import com.enkod.enkodpushlibrary.Variables.actionButtonsUrl
import java.util.*


internal fun NotificationCompat.Builder.setIcon(context: Context, data: String?): NotificationCompat.Builder {
    fun defaultResID() = context.getResourceFromMeta("com.google.firebase.messaging.default_notification_icon", defaultIconId)

    if (data != null){

        val resID = EnkodPushLibrary.getResourceId(context, data, "drawable", context.packageName)
        if(resID > 0){
            setSmallIcon(resID)
        }else {
            setSmallIcon(defaultResID())
        }

    }else{

        setSmallIcon(defaultResID())
    }
    return this
}


private fun Context.getResourceFromMeta(path: String, default: Int): Int {
     return packageManager
        .getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        .run {
            metaData.getInt(path, default)
        }
}


internal fun NotificationCompat.Builder.setVibrate(boolean: Boolean): NotificationCompat.Builder {
    if(boolean){

        setVibrate(EnkodPushLibrary.vibrationPattern)
    }else {

        setVibrate(longArrayOf())
    }
    return this
}

internal fun NotificationCompat.Builder.setLights(
    ledColor: String?,
    ledOnMs: String?,
    ledOffMs: String?
): NotificationCompat.Builder {
    if(ledColor != null){

        setLights(
            Color.parseColor(ledColor),
            ledOnMs?.toIntOrNull() ?: 100,
            ledOffMs?.toIntOrNull() ?: 100
        )
    }else{

    }
    return this
}

internal fun NotificationCompat.Builder.setSound(defaultSound: Boolean): NotificationCompat.Builder {
    if(defaultSound){

        setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
    }else {

    }
    return this
}

@RequiresApi(Build.VERSION_CODES.O)
internal fun NotificationChannel.enableLights(s: String?) {
    if (s.isNullOrEmpty()) { return }
    try {
        lightColor = Color.parseColor(s)
        enableLights(true)
    }catch (e: Exception){
        e.printStackTrace()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
internal fun NotificationChannel.setSound(hasSound: Boolean) {
    if(hasSound) {
        setSound(
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build()
        )
    }else{
        setSound(null, null)
    }
}

internal fun NotificationCompat.Builder.addActions(context: Context, map: Map<String, String>): NotificationCompat.Builder {

    for (i in 1 .. 3){
        if(map.containsKey("${actionButtonText}$i")) {


            val intent = EnkodPushLibrary.getIntent(
                context = context,
                data = map,
                field = map["${actionButtonIntent}$i"] ?: "",
                url = map["${actionButtonsUrl}$i"] ?: ""
            )

            val text = "${actionButtonText}$i"
            addAction(0, map[text], intent)
        }
    }
    return this
}