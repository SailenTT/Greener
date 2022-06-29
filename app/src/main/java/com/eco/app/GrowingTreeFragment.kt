package com.eco.app

import android.Manifest
import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
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
import kotlin.properties.Delegates

class GrowingTreeFragment : Fragment() {
    private lateinit var binding: FragmentGrowingTreeBinding
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
        binding=FragmentGrowingTreeBinding.inflate(inflater,container,false)

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

        binding.button2.setOnClickListener {
            val intent = Intent(context,StepService::class.java)
            activity!!.startService(intent)
        }

        binding.button3.setOnClickListener{
            val intent = Intent(context,StepService::class.java)
            activity!!.stopService(intent)
        }

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
}