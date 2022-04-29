package com.eco.app

import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.BounceInterpolator
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
    private var firstMove=true
    private var yStart= 0.0F
    private var gameRunning=false
    private lateinit var trashBin: ImageView
    private var score=0
    private val spriteList= listOf<ImageView>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //Inflate the binding for this fragment
        binding= FragmentTrashBinGameBinding.inflate(inflater,container,false)

        trashBin=binding.trashBin

        binding.startGameButton.setOnClickListener {
            startGame()
        }

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

                val newX = trashBin.x + (motion.x-xStart)

                if (newX < 0F) {
                    trashBin.x = 0F
                } else if (newX > ((binding.root.width - trashBin.width).toFloat())) {
                    trashBin.x = (binding.root.width - trashBin.width).toFloat()
                } else {
                    trashBin.x = newX
                }
                return true
            }
        }
        return true
    }


    fun startGame(){

        binding.startGameButton.visibility=View.INVISIBLE
        binding.gameOverScreen.visibility=View.INVISIBLE
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
                for(i in 0..2){
                val img_falling_sprite = ImageView(requireContext())

                val metrics = requireContext().resources.displayMetrics

                img_falling_sprite.setImageResource(R.drawable.crumbled_paper)

                binding.relativeLayout.addView(img_falling_sprite)

                img_falling_sprite.layoutParams.height = (metrics.density * 60).toInt()
                img_falling_sprite.layoutParams.width = (metrics.density * 60).toInt()
                img_falling_sprite.x =
                    Random.nextInt((binding.root.width - (metrics.density * 60).toInt()))
                        .toFloat()
                img_falling_sprite.tag = false

                //Falling tag
                //TODO aggiungere l'accellerate interpolator dopo un certo score
                //volendo si può usare il bounce per far riprendere i primi e poi toglierlo

                //non faccio sparire la carta per far capire all'utente che ha inquinato
                img_falling_sprite.animate()
                    .translationY(binding.root.height - img_falling_sprite.layoutParams.height.toFloat())
                    //.setInterpolator(AccelerateInterpolator())
                    //.setInterpolator(BounceInterpolator())
                    .setDuration(3000)
                    .withEndAction {
                        //TODO far finire il gioco
                        println("game over")
                        //fermo il cestino
                        img_falling_sprite.tag = true
                        img_falling_sprite.layoutParams.height = img_falling_sprite.layoutParams.height-50
                        img_falling_sprite.layoutParams.width = img_falling_sprite.layoutParams.width-48
                        img_falling_sprite.y = img_falling_sprite.y + 100
                        //faccio comparire la schermata di fine
                        trashBin.setOnTouchListener(null)
                        binding.txtFinalScore.text=0.toString()
                        binding.restartGameButton.setOnClickListener{startGame()}
                        binding.gameOverScreen.visibility = View.VISIBLE
                    }
                //.setInterpolator(BounceInterpolator())

                //Thread che rileva la collisione
                img_falling_sprite.post {
                    Thread {
                        run {
                            /*lateinit var imgLocation: IntArray
                            img_falling_sprite.getLocationOnScreen(imgLocation)
                            val rect=Rect(imgLocation[0],imgLocation[1],img_falling_sprite.width+imgLocation[0],img_falling_sprite.height+imgLocation[1])*/
                            var falling = true
                            var rect1=Rect()
                            var rect2=Rect()
                            img_falling_sprite.getHitRect(rect1)
                            trashBin.getHitRect(rect2)

                            println("rect1 width=${rect1.width()}; rec1.height=${rect1.height()};\nrect2 width=${rect2.width()} rect2 heigth=${rect2.height()}")
                            while (falling) {
                                //println("${img_falling_sprite.y+img_falling_sprite.height}; ${trashBin.y}")

                                if (rect1.intersect(rect2)) {
                                    println("intersezione!!!; img_falling_sprite: ${img_falling_sprite.y + img_falling_sprite.height}")
                                    img_falling_sprite.animation.cancel()
                                    img_falling_sprite.animate()
                                        .translationX(trashBin.x + trashBin.width / 2 - img_falling_sprite.width / 2)
                                        .translationY(trashBin.y + img_falling_sprite.y - img_falling_sprite.height / 2)
                                        .alpha(1f)
                                        .setDuration(1200)
                                        .withEndAction {
                                            binding.root.removeView(img_falling_sprite)
                                            score++
                                            binding.txtScore.text = score.toString()
                                            falling = false
                                        }
                                } else if (img_falling_sprite.tag == true) {
                                    falling = false
                                }
                                /*if(img_falling_sprite.y+img_falling_sprite.height==trashBin.y){
                                    println("collisione possibile\n\n\n\n")
                                    if((img_falling_sprite.x>=trashBin.x)&&(img_falling_sprite.x<=trashBin.x+trashBin.width)){
                                        println("Collissione\n\n\n")
                                    }
                                }*/
                            }
                        }
                    }.start()

                }}
            }

        }
    }
}