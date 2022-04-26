package com.eco.app

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //Inflate the binding for this fragment
        binding= FragmentTrashBinGameBinding.inflate(inflater,container,false)

        binding.trashBin.setOnTouchListener(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*binding= FragmentTrashBinGameBinding.inflate(layoutInflater)
        binding.trashBin.setOnTouchListener(this)*/

        view.findViewById<ImageView>(R.id.trash_bin).setOnTouchListener(this)
    }

    override fun onTouch(view: View?, motion: MotionEvent?): Boolean {
        when(motion?.action){
            /*MotionEvent.ACTION_DOWN->{
                begin= System.nanoTime()
                xStart=motion.x
                yStart=motion.y
                return true
            }*/
            MotionEvent.ACTION_MOVE->{
                //val elapsedSec=(System.nanoTime()-begin)*1e-9
                //var xDiff= motion.x-xStart
                //var yDiff= motion.y-yStart

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
                //if(xDiff!=0F) {
                val trashBin = binding.trashBin
                val oldX=trashBin.x
                val newX=oldX+motion.x

                if(newX<0F){
                    trashBin.x=0F
                }
                else if(newX>((binding.root.width-trashBin.width).toFloat())){
                    trashBin.x=(binding.root.width-trashBin.width).toFloat()
                }
                else{
                    trashBin.x=newX
                }

                //}
            }
        }
        return true
    }
}