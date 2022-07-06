package com.eco.app.calendar

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.eco.app.R
import java.util.*

const val notificationID = 123
const val channelID = "notificationCalendarChannel"
const val messageExtra = "messageExtra"



class NotificationCalendar : BroadcastReceiver() {
    private  lateinit var lunedi : String
    private lateinit var martedi : String
    private lateinit var mercoledi : String
    private lateinit var giovedi : String
    private lateinit var venerdi : String
    private lateinit var sabato : String
    private lateinit var domenica : String
    private lateinit var calendar: Calendar
    private lateinit var what : String

    override fun onReceive(context: Context, intent: Intent) {
        val sharedprefs = context.getSharedPreferences(CalendarFragment.SHARED_PREFS,Context.MODE_PRIVATE)
        lunedi = sharedprefs.getString("lunedi","0").toString()
        martedi = sharedprefs.getString("martedi","0").toString()
        mercoledi = sharedprefs.getString("mercoledi","0").toString()
        giovedi = sharedprefs.getString("giovedi","0").toString()
        venerdi = sharedprefs.getString("venerdi","0").toString()
        sabato = sharedprefs.getString("sabato","0").toString()
        domenica = sharedprefs.getString("domenica","0").toString()
        calendar = Calendar.getInstance()

        when(calendar.get(Calendar.DAY_OF_WEEK)){
            Calendar.MONDAY -> what = lunedi
            Calendar.TUESDAY -> what = martedi
            Calendar.WEDNESDAY -> what = mercoledi
            Calendar.THURSDAY -> what = giovedi
            Calendar.FRIDAY -> what = venerdi
            Calendar.SATURDAY -> what = sabato
            Calendar.SUNDAY -> what = domenica
        }


        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.ic_person)
            .setContentTitle(context.resources.getString(R.string.calendar_notification_title))
            .setContentText(context.resources.getString(R.string.calendar_notification_body)+" "+what)
            val manager = NotificationManagerCompat.from(context)
            manager.notify(notificationID,notification.build())
    }
}