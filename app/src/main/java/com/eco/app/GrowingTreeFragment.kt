package com.eco.app

import android.Manifest
import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.eco.app.databinding.FragmentGrowingTreeBinding
import com.google.android.material.transition.MaterialContainerTransform
import kotlin.properties.Delegates

class GrowingTreeFragment : Fragment(),SensorEventListener {
    private lateinit var binding: FragmentGrowingTreeBinding
    private var sensorManager : SensorManager? = null
    private var running : Boolean = false
    private var steps : Int = 0
    private var totalSteps by Delegates.notNull<Int>()
    private var previousTotalSteps : Float = 0f
    private lateinit var wateringCan: LottieAnimationView
    private var startX: Float = 0F
    private var startY: Float = 0F
    private val minFrame=23
    val LAST_FRAME_STEPS=55000

    //TODO salvare il progresso di questo gioco nel db di firebase
    //TODO mettere il punteggio del gioco nel db di firebase
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedElementEnterTransition = MaterialContainerTransform()

        binding=FragmentGrowingTreeBinding.inflate(inflater,container,false)
        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager //getto il sensore

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { //richiesta permesso
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                101
            )
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACTIVITY_RECOGNITION)
            != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "Permission not granted for the tracking", Toast.LENGTH_SHORT).show()
        }

        wateringCan=binding.wateringCan

        wateringCan.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?) {
                wateringCan.setMinFrame(minFrame)
                wateringCan.progress = 0F
                wateringCan.playAnimation()
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationStart(animation: Animator?) {}
        })

        wateringCan.setOnTouchListener { v, event ->touchListener(v,event)  }

        return binding.root
    }

    fun touchListener(v: View, event: MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN->{
                startX = event.x
                startY = event.y
                return true
            }
            MotionEvent.ACTION_MOVE->{
                val deltaX = event.x - startX
                val deltaY = event.y - startY
                wateringCan.x += deltaX
                wateringCan.y += deltaY
                if (wateringCan.x >= (binding.wateringCanContainer.x + binding.wateringCanContainer.width)
                    ||wateringCan.y>= (binding.wateringCanContainer.y + binding.wateringCanContainer.height)
                    ||wateringCan.y+wateringCan.height<=binding.wateringCanContainer.y){

                    if (!wateringCan.isAnimating) {
                        wateringCan.playAnimation()
                    }
                }
                return true
            }
            MotionEvent.ACTION_UP-> {
                //resetto la posizione dell'oggetto
                wateringCan.cancelAnimation()
                wateringCan.setMinFrame(0)
                wateringCan.progress=0F
                wateringCan.animate()
                    .translationY(0F)
                    .translationX(0F)
                return true
            }
        }
        return true
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
           //binding.totalSteps.text = event!!.values[0].toString()
            steps = event!!.values[0].toInt() //steps nella sessione
            totalSteps = getSteps() //getto gli steps totali
            totalSteps += steps //aggiungo quelli fatti mo
            saveSteps(totalSteps) //li salvo

       }
    }

    private fun getSteps() : Int {
      val sharedPreferences = activity!!.getSharedPreferences("trackingPrefs", Context.MODE_PRIVATE)
      val totalsteps = sharedPreferences!!.getInt("steps",0)
      return totalsteps
    }

    private fun saveSteps(steps : Int) {
        val sharedPreferences =
            activity?.getSharedPreferences("trackingPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.apply {
            putInt("steps",steps );
        }?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        //Toast.makeText(requireContext(), "Hai fatto "+totalSteps+"passi", Toast.LENGTH_SHORT).show()
    }
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}
}