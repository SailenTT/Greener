package com.eco.app

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.eco.app.databinding.FragmentTrashBinGameBinding

/**
 * A simple [Fragment] subclass.
 * Use the [TrashBinGame.newInstance] factory method to
 * create an instance of this fragment.
 */
class TrashBinGame : Fragment(), View.OnTouchListener {
    private lateinit var binding: FragmentTrashBinGameBinding
    private val SWIPE_TRESHOLD: Int= 100
    private val SWIPE_VELOCITY_THRESHOLD: Int= 200
    private var begin= 0L
    private var xStart= 0.0F
    private var yStart= 0.0F


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= FragmentTrashBinGameBinding.inflate(layoutInflater)

        binding.trashBin.setOnTouchListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trash_bin_game, container, false)
    }

    override fun onTouch(view: View?, motion: MotionEvent?): Boolean {
        when(motion?.action){
            MotionEvent.ACTION_DOWN->{
                begin= System.nanoTime()
                xStart=motion.x
                yStart=motion.y
                return true
            }
            MotionEvent.ACTION_MOVE->{
                val elapsedSec=(System.nanoTime()-begin)*1e-9
                var xDiff= motion.x-xStart
                var yDiff= motion.y-yStart

                /*if(abs(xDiff)> abs(yDiff)){
                    if(abs(xDiff)<SWIPE_TRESHOLD){
                        return false
                    }
                    if(abs(xDiff)/elapsedSec<SWIPE_VELOCITY_THRESHOLD){
                        if(xDiff>0) {
                            //movimento a destra
                        }
                        else{
                            //movimento a sinistra
                        }
                    }
                }*/
                if(xDiff!=0F) {
                    val trashBin = binding.trashBin
                    trashBin.x = motion.x
                }

            }
        }
        return true
    }
}