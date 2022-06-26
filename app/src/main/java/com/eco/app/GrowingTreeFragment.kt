package com.eco.app

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.eco.app.databinding.FragmentGrowingTreeBinding

class GrowingTreeFragment : Fragment(),SensorEventListener {
    private lateinit var binding: FragmentGrowingTreeBinding
    private var sensorManager : SensorManager? = null
    private var running : Boolean = false
    private var totalSteps : Float= 0f
    private var previousTotalSteps : Float = 0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentGrowingTreeBinding.inflate(inflater,container,false)
        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager //getto il sensore
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        running = true
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) //getto il sensore di tipo contapassi
        if(stepSensor == null){ //se null, il device proprio non ha il sensore
            Toast.makeText(context, "Il tuo device non ha un sensore per contare i passi", Toast.LENGTH_SHORT).show()
        }else{
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        running = false
        sensorManager?.unregisterListener(this) //non registra piu eventi il sensore quando l'activity va in pause
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(running){
           Toast.makeText(context, "running", Toast.LENGTH_SHORT).show()
           //totalSteps = event!!.values[0]
           //val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
           binding.totalSteps.text = event!!.values[0].toString()
       }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        TODO("Not yet implemented")
    }
}