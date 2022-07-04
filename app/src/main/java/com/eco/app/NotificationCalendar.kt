package com.eco.app

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

const val notificationID = 123
const val channelID = "notificationCalendarChannel"
const val messageExtra = "messageExtra"



class NotificationCalendar : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.ic_person)
            .setContentTitle(intent.getStringExtra("CalendarTitle").toString())
            .setContentText(intent.getStringExtra("CalendarBody").toString())
        val manager = NotificationManagerCompat.from(context)
        manager.notify(notificationID,notification.build())

    }
}