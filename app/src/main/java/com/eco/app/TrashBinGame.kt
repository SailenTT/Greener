package com.eco.app

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.eco.app.databinding.FragmentTrashBinGameBinding
import kotlin.random.Random


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
    private var gameRunning=false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //Inflate the binding for this fragment
        binding= FragmentTrashBinGameBinding.inflate(inflater,container,false)

        binding.startGameButton.setOnClickListener {
            startGame()
        }

        return binding.root
    }

    override fun onTouch(view: View?, motion: MotionEvent?): Boolean {
        when(motion?.action){
            /*MotionEvent.ACTION_DOWN->{
                begin= System.nanoTime()
                xStart=motion.x
                yStart=motion.y
                return true
            }*/
            MotionEvent.ACTION_DOWN->{
                println(motion.x)
                xStart=motion.x
                return true
            }
            MotionEvent.ACTION_UP->{
                return true
            }
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
                val newX=oldX+motion.x-xStart
                xStart=0F
                println(motion.x)

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
                return true
            }
        }
        return true
    }

    fun startGame(){

        binding.startGameButton.visibility=View.INVISIBLE
        //Toast.makeText(context,"Gioco Startato",Toast.LENGTH_SHORT).show()

        gameRunning=true

        /*while(gameRunning){
            //faccio spawnare la sprite che cade
            val img_falling_sprite=ImageView(requireContext())

            val metrics =requireContext().resources.displayMetrics

            img_falling_sprite.setImageResource(R.drawable.crumbled_paper)

            binding.relativeLayout.addView(img_falling_sprite)

            img_falling_sprite.layoutParams.height=(metrics.density*60).toInt()
            img_falling_sprite.layoutParams.width=(metrics.density*60).toInt()


        }*/

        binding.trashBin.setOnTouchListener(this)

        activity?.runOnUiThread{
            run {
                for(i in 0..3){
                    println("run on ui")
                    val img_falling_sprite=ImageView(requireContext())

                    val metrics =requireContext().resources.displayMetrics

                    img_falling_sprite.setImageResource(R.drawable.crumbled_paper)

                    binding.relativeLayout.addView(img_falling_sprite)

                    img_falling_sprite.layoutParams.height=(metrics.density*60).toInt()
                    img_falling_sprite.layoutParams.width=(metrics.density*60).toInt()
                    img_falling_sprite.x=Random.nextInt((requireContext().resources.displayMetrics.widthPixels-img_falling_sprite.x).toInt()).toFloat()

                    println(img_falling_sprite.x)
                }
            }
        }
    }



}