package com.eco.app

import android.Manifest
import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.eco.app.databinding.FragmentGrowingTreeBinding
import com.google.android.material.transition.MaterialContainerTransform

class GrowingTreeFragment : Fragment() {
    private lateinit var binding: FragmentGrowingTreeBinding
    private lateinit var wateringCan: LottieAnimationView
    private lateinit var treeImg : ImageView
    private var startX: Float = 0F
    private var startY: Float = 0F
    private var totalSteps : Long=0
    private val minFrame=23
    companion object{
        const val treeImgPrefix="growing_tree_frame_"
        val stepsLevels= listOf(0L,4615L,9230L,13845L,18460L,23075L,27690L,32305L,36920L,41535L,46150L,50765L,55380L,60000L)
        /*private val STEPS_LEVEL_1=4615
        private val STEPS_LEVEL_2=9230
        private val STEPS_LEVEL_3=13845
        private val STEPS_LEVEL_4=18460
        private val STEPS_LEVEL_5=23075
        private val STEPS_LEVEL_6=27690
        private val STEPS_LEVEL_7=32305
        private val STEPS_LEVEL_8=36920
        private val STEPS_LEVEL_9=41535
        private val STEPS_LEVEL_10=46150
        private val STEPS_LEVEL_11=50765
        private val STEPS_LEVEL_12=55380
        private val STEPS_LEVEL_13=60000*/
    }

    //TODO salvare il progresso di questo gioco nel db di firebase
    //TODO mettere il punteggio del gioco nel db di firebase
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedElementEnterTransition = MaterialContainerTransform()

        binding=FragmentGrowingTreeBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //requireContext().getSharedPreferences("trackingPrefs",Context.MODE_PRIVATE).edit().remove("steps").commit()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { //richiesta permesso
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                101
            )
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACTIVITY_RECOGNITION)
            != PackageManager.PERMISSION_GRANTED) {
            //TODO dire all'utente che non può giocare al gioco
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


        startStepService()

        binding.totalSteps.text=totalSteps.toString()

        treeImg=binding.treeImageView

        setTree()

    }

    fun setTree(){
        val sharedPref=requireContext().getSharedPreferences("trackingPrefs",Context.MODE_PRIVATE)
        totalSteps= sharedPref.getLong("steps",0L)

        println(totalSteps)
        var tree_frame= (stepsLevels.size-1)/2
        //TODO vedere se sta roba va
        if(totalSteps>=stepsLevels[stepsLevels.size-1]){
           tree_frame= stepsLevels.size-1
        }
        else{
            while(!(totalSteps>=stepsLevels[tree_frame]&&totalSteps<=stepsLevels[tree_frame+1])){
                if (totalSteps<stepsLevels[tree_frame]) {
                    tree_frame/=2
                    println("diviso per 2: $tree_frame")
                }
                else if (totalSteps>stepsLevels[tree_frame]){
                    tree_frame=((tree_frame+stepsLevels.size-1)/2)
                }
            }
        }

        var uri="@drawable/$treeImgPrefix$tree_frame"
        var imgRes=resources.getIdentifier(uri,null,requireActivity().packageName)
        //TODO quando avrò tutti i frame sistemare questa parte
        if(imgRes==0){
            uri="@drawable/$treeImgPrefix"+8
            imgRes=resources.getIdentifier(uri,null,requireActivity().packageName)
        }
        val newImg=ResourcesCompat.getDrawable(resources,imgRes,null)
        treeImg.setImageDrawable(newImg)

    }

    private fun startStepService() {
        val intent = Intent(context,StepService::class.java)
        activity!!.startService(intent)
    }

    fun touchListener(v: View, event: MotionEvent): Boolean {
        //TODO sarebbe meglio sotituire il wateringCan con v
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