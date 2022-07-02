package com.eco.app
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import kotlin.properties.Delegates

class StepService : Service() ,SensorEventListener{
    private var sensorManager : SensorManager? = null
    private var running : Boolean = false
    private var steps : Int = 0
    private var totalSteps by Delegates.notNull<Int>()
    private val ACTION_STOP_LISTEN = "action_stop_listen"

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager //getto il sensore

    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent != null && ACTION_STOP_LISTEN.equals(intent.action)){
            stopForeground(true)
            stopSelf()
            return START_NOT_STICKY
        }
        createNotificationChannel()
        val notificationIntent = Intent(this, GrowingTreeFragment::class.java)
        val stopself = Intent(this,StepService::class.java).setAction(ACTION_STOP_LISTEN)
        val stopPending = PendingIntent.getService(this,123,stopself,PendingIntent.FLAG_UPDATE_CURRENT)
        val pendingIntent = PendingIntent.getService(this, 0, notificationIntent, 0)
        val notification = NotificationCompat.Builder(this, "1")
            .setContentTitle("Greener")
            .setContentText("Tracking steps..")
            .setSmallIcon(R.drawable.ic_person)
            .setContentIntent(pendingIntent)
            .addAction(R.mipmap.ic_launcher,"Stop",stopPending)
            .build()

        startForeground(1, notification)
        running = true
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) //getto il sensore di tipo contapassi
        if(stepSensor == null){ //se null, il device proprio non ha il sensore
            Toast.makeText(this, "Il tuo device non ha un sensore per contare i passi", Toast.LENGTH_SHORT).show()
        }else{
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
      //  Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show()
        return super.onStartCommand(intent, flags, startId)
    }
    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val serviceChannel = NotificationChannel("1","Tracking service", NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }

    }

    override fun onDestroy() {
        running = false
       // Toast.makeText(this, "$totalSteps", Toast.LENGTH_SHORT).show()
        super.onDestroy()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(running){
            Log.d("Tracking","Sto trackando")
           // Toast.makeText(this, "Running", Toast.LENGTH_SHORT).show()
            //totalSteps = event!!.values[0]
            //val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
            //binding.totalSteps.text = event!!.values[0].toString()
            steps = event!!.values[0].toInt() //steps nella sessione
            totalSteps = getSteps() //getto gli steps totali
            totalSteps += steps //aggiungo quelli fatti mo
            saveSteps(totalSteps) //li salvo
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    private fun getSteps() : Int {
        val sharedPreferences = getSharedPreferences("trackingPrefs", Context.MODE_PRIVATE)
        val totalsteps = sharedPreferences!!.getInt("steps",0)
        return totalsteps
    }

    private fun saveSteps(steps : Int) {
        val sharedPreferences =
            getSharedPreferences("trackingPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.apply {
            putInt("steps",steps );
        }?.apply()
    }

}