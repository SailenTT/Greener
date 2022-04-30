package com.eco.app

import android.animation.ValueAnimator
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.BounceInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
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
    private lateinit var trashBin: RelativeLayout
    private var score=0
    private var last_falling_sprite: ImageView?=null
    private lateinit var lottie: LottieAnimationView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //Inflate the binding for this fragment
        binding= FragmentTrashBinGameBinding.inflate(inflater,container,false)

        trashBin=binding.trashBinContainer

        binding.startGameButton.setOnClickListener {
            startGame()
        }

        lottie=binding.lottie
        lottie.speed*=2

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


    fun startGame() {

        binding.startGameButton.visibility = View.INVISIBLE
        binding.gameOverScreen.visibility = View.INVISIBLE
        //Toast.makeText(context,"Gioco Startato",Toast.LENGTH_SHORT).show()
        binding.txtFinalScore.text = 0.toString()
        score=0

        binding.relativeLayout.removeView(last_falling_sprite)

        trashBin.x=0F

        binding.trashBin.setOnTouchListener(this)

        println("spawna palla ora")
        spawnFallingTrash()

    }

    fun spawnFallingTrash(){
        activity?.runOnUiThread{
            run {
                val img_falling_sprite = ImageView(requireContext())
                last_falling_sprite=img_falling_sprite

                val metrics = requireContext().resources.displayMetrics

                img_falling_sprite.setImageResource(R.drawable.crumbled_paper)

                binding.relativeLayout.addView(img_falling_sprite)

                img_falling_sprite.layoutParams.height = (metrics.density * 60).toInt()
                img_falling_sprite.layoutParams.width = (metrics.density * 60).toInt()
                img_falling_sprite.x =
                    Random.nextInt((binding.root.width - (metrics.density * 60).toInt()))
                        .toFloat()

                //Falling tag
                //TODO aggiungere l'accellerate interpolator dopo un certo score
                //volendo si può usare il bounce per far riprendere i primi e poi toglierlo

                //non faccio sparire la carta per far capire all'utente che ha inquinato

                img_falling_sprite.animate()
                    .translationY(binding.root.height - img_falling_sprite.layoutParams.height.toFloat())
                    //.setInterpolator(AccelerateInterpolator())
                    //.setInterpolator(BounceInterpolator())
                    .setDuration(6000)
                    .setUpdateListener {
                        //controllo se non sta più cadendo
                        if (img_falling_sprite.tag == false) {
                            println("faccio partire l'animazione di cattura")
                            objectCatched(img_falling_sprite)
                        }
                    }
                    .withEndAction {
                        println("game over")
                        //fermo il cestino
                        img_falling_sprite.tag = false
                        /*img_falling_sprite.layoutParams.height =
                            img_falling_sprite.layoutParams.height - 50
                        img_falling_sprite.layoutParams.width =
                            img_falling_sprite.layoutParams.width - 48
                        img_falling_sprite.y = img_falling_sprite.y + 100*/
                        //faccio comparire la schermata di fine
                        trashBin.setOnTouchListener(null)
                        binding.txtFinalScore.text="Hai fatto "+score.toString()+" punti"
                        binding.restartGameButton.setOnClickListener { startGame() }
                        binding.gameOverScreen.visibility = View.VISIBLE
                    }


                //Thread che rileva la collisione
                img_falling_sprite.post {
                    Thread {
                        //uso il tag per dire se l'oggetto sta cadendo o no
                        img_falling_sprite.tag = true
                        /*var rect1 = Rect()
                        var rect2 = Rect()
                        img_falling_sprite.getHitRect(rect1)
                        trashBin.getHitRect(rect2)*/
                        while (img_falling_sprite.tag == true) {

                            //if (rect1.intersect(rect2)) {
                            if (img_falling_sprite.y + img_falling_sprite.height >= trashBin.y+(trashBin.height/8) && img_falling_sprite.y + img_falling_sprite.height <= trashBin.y + (trashBin.height / 4)) {
                                if ((img_falling_sprite.x >= trashBin.x && img_falling_sprite.x <= trashBin.x + trashBin.width) || (img_falling_sprite.x + img_falling_sprite.width >= trashBin.x && img_falling_sprite.x + img_falling_sprite.width <= trashBin.x + trashBin.width)) {
                                    println("intersezione!!!; img_falling_sprite: ${img_falling_sprite.y + img_falling_sprite.height}")
                                    //img_falling_sprite.animation.cancel()
                                    img_falling_sprite.tag = false
                                    spawnFallingTrash()
                                }
                            }
                        }
                    }.start()

                }
            }

        }
    }

    fun objectCatched(img_falling_sprite: ImageView){
        img_falling_sprite.animate().setUpdateListener(null)
        img_falling_sprite.clearAnimation()
        score++
        //TODO al posto che punti caricare la stirnga dal file di stringhe (per le possibili traduzioni
        binding.txtScore.text = score.toString() + " punti"

        /*lottie.x=trashBin.x+((trashBin.width-lottie.width)/2)
        lottie.y=trashBin.y+((trashBin.height-lottie.height)/2)
        lottie.visibility=View.VISIBLE*/
        //lottie.setAnimation(R.raw.recycle_animation)
        lottie.playAnimation()
        //lottie.repeatCount=2

        img_falling_sprite.animate()
            .translationX(trashBin.x + trashBin.width / 2 - img_falling_sprite.width / 2)
            .translationY(trashBin.y + img_falling_sprite.height / 2)
            .alpha(1f)
            .setDuration(100)
            .withEndAction {
                binding.relativeLayout.removeView(img_falling_sprite)
            }
        //img_falling_sprite.tag=true
    }
}