package com.eco.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.eco.app.databinding.FragmentGarbageSorterGameBinding

class GarbageSorterGame : Fragment(), View.OnTouchListener{
    private lateinit var binding : FragmentGarbageSorterGameBinding
    private var xStart= 0.0F


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentGarbageSorterGameBinding.inflate(layoutInflater,container,false)

        return binding.root
    }

    override fun onTouch(view: View?, motion: MotionEvent?): Boolean {
        when(motion?.action){
            MotionEvent.ACTION_DOWN->{
                xStart=motion.x
                return true
            }
            MotionEvent.ACTION_UP->{
                return true
            }
            MotionEvent.ACTION_MOVE->{

                /*val newX = trashBin.x + (motion.x-xStart)

                if (newX < 0F) {
                    trashBin.x = 0F
                } else if (newX > ((binding.root.width - trashBin.width).toFloat())) {
                    trashBin.x = (binding.root.width - trashBin.width).toFloat()
                } else {
                    trashBin.x = newX
                }*/
                return true
            }
        }
        return true
    }
}