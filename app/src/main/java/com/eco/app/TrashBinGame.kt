package com.eco.app

import android.animation.Animator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
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
    private var xStart= 0.0F
    private lateinit var trashBinContainer: RelativeLayout
    private var score=0
    private var last_falling_sprite: ImageView?=null
    private lateinit var lottie: LottieAnimationView
    private val defaultSpeed=2700L
    private val minimumSpeed=900L
    private val defaultInclination=400
    private val maxXInclination=1000
    private var firstStart=true
    private var gameRunning=false
    private val spawnDelay=1400L
    private val minimumSpawnDelay=900L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //Inflate the binding for this fragment
        binding= FragmentTrashBinGameBinding.inflate(inflater,container,false)

        trashBinContainer=binding.trashBinContainer

        binding.startGameButton.setOnClickListener {
            startGame()
        }

        lottie=binding.lottie

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

                val newX = trashBinContainer.x + (motion.x-xStart)

                if (newX < 0F) {
                    trashBinContainer.x = 0F
                } else if (newX > ((binding.root.width - trashBinContainer.width).toFloat())) {
                    trashBinContainer.x = (binding.root.width - trashBinContainer.width).toFloat()
                } else {
                    trashBinContainer.x = newX
                }
                return true
            }
        }
        return true
    }


    fun startGame() {

        binding.startGameInstructionsContainer.visibility=View.INVISIBLE
        binding.gameOverScreen.visibility = View.INVISIBLE
        //Toast.makeText(context,"Gioco Startato",Toast.LENGTH_SHORT).show()
        binding.txtScore.text = 0.toString()
        score=0

        binding.relativeLayout.removeView(last_falling_sprite)

        val layoutParams=(trashBinContainer.layoutParams as RelativeLayout.LayoutParams)
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
        trashBinContainer.layoutParams=layoutParams

        trashBinContainer.setOnTouchListener(this)

        gameRunning=true

        if(firstStart) {
            val lottieSwipeAnimation=binding.lottieSwipeAnimation
            lottieSwipeAnimation.visibility=View.VISIBLE

            lottieSwipeAnimation.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator?) {
                    binding.mainContainer.alpha = 0.70F
                    lottieSwipeAnimation.alpha = 1F
                }

                override fun onAnimationCancel(p0: Animator?) {
                }

                override fun onAnimationRepeat(p0: Animator?) {
                }

                override fun onAnimationEnd(p0: Animator?) {
                    lottieSwipeAnimation.visibility = View.INVISIBLE
                    binding.mainContainer.alpha = 1F

                    startSpawn()

                }
            })

            firstStart=false

            lottieSwipeAnimation.playAnimation()
        }
        else{
            startSpawn()
        }

    }

    fun startSpawn(){
        Thread{
            Thread.sleep(200)
            while(gameRunning) {
                println("spawna il prossimo")

                //Fai partire il gioco dopo tot millisecondi
                spawnFallingTrash()
                var newDelay=spawnDelay-(score*3)
                if(newDelay<minimumSpawnDelay){
                    newDelay=minimumSpawnDelay
                }
                Thread.sleep(newDelay)
            }
        }.start()
    }

    fun spawnFallingTrash(){
        activity?.runOnUiThread{
            run {
                val img_falling_sprite = ImageView(requireContext())
                last_falling_sprite=img_falling_sprite

                val metrics = requireContext().resources.displayMetrics

                img_falling_sprite.setImageResource(R.drawable.crumbled_paper)

                img_falling_sprite.tag=true

                binding.relativeLayout.addView(img_falling_sprite)

                var ballSize=(metrics.density * 55).toInt()

                if(score>=100){
                    img_falling_sprite.y=0+((Random.nextInt(200)+100)*metrics.density)
                }

                if(score>=80){
                    ballSize-=ballSize/3
                }
                else if(score>=60){
                    ballSize -= (ballSize / 4)
                }
                else if(score>=40){
                    ballSize-=(ballSize/5)
                }
                else if(score>=25){
                    ballSize-=(ballSize/6)
                }

                img_falling_sprite.layoutParams.height = ballSize
                img_falling_sprite.layoutParams.width = ballSize
                img_falling_sprite.x =
                    Random.nextInt((binding.root.width - ballSize))
                        .toFloat()

                //Falling tag
                //volendo si può usare il bounce per far riprendere i primi e poi toglierlo

                //non faccio sparire la carta per far capire all'utente che ha inquinato
                var ballXMovement: Float?=null

                val spriteAnimation=img_falling_sprite.animate()
                    .translationY(binding.root.height - (img_falling_sprite.layoutParams.height/2).toFloat())
                    .rotationBy(280F)
                    .setDuration(defaultSpeed)
                    .withEndAction {
                        if(img_falling_sprite.tag==true&&gameRunning) {
                            println("game over")
                            //fermo il cestino
                            img_falling_sprite.tag = false
                            gameRunning=false
                            //faccio comparire la schermata di fine
                            trashBinContainer.setOnTouchListener(null)
                            binding.txtFinalScore.text = "Hai fatto " + score.toString() + " punti"
                            score=0
                            binding.restartGameButton.setOnClickListener { startGame() }
                            binding.gameOverScreen.visibility = View.VISIBLE
                        }
                    }
                spriteAnimation.setUpdateListener {value->
                    //controllo se non sta più cadendo
                    if(gameRunning) {
                        if (img_falling_sprite.tag == false) {
                            println("faccio partire l'animazione di cattura")
                            objectCatched(img_falling_sprite)
                        }
                        if ((img_falling_sprite.x + ballSize) > binding.root.width) {
                            img_falling_sprite.x = (binding.root.width - ballSize).toFloat()
                            if (ballXMovement != null) {
                                spriteAnimation.translationXBy(-ballXMovement!!)
                            } else {
                                spriteAnimation.translationXBy(0F)
                            }
                        } else if (img_falling_sprite.x < 0) {
                            if (ballXMovement != null) {
                                img_falling_sprite.x = 0F
                                spriteAnimation.translationXBy(-ballXMovement!!)
                            } else {
                                spriteAnimation.translationXBy(0F)
                            }
                        }
                    }
                    else{
                        img_falling_sprite.clearAnimation()
                        img_falling_sprite.animate().setUpdateListener(null)
                        binding.root.removeView(img_falling_sprite)
                    }
                }

                if(score>=10){
                    val currentSpeed=defaultSpeed-(score*18)
                    if(currentSpeed>=minimumSpeed) {
                        spriteAnimation.duration = currentSpeed
                    }
                    else{
                        spriteAnimation.duration=minimumSpeed
                    }
                }

                if(score>=25){
                    if(defaultInclination+(score*10)>maxXInclination) {
                        ballXMovement =
                            (Random.nextInt((defaultInclination+(score*10)) * 2) - (defaultInclination+(score*15))) * metrics.density
                    }
                    else{
                        ballXMovement =
                            (Random.nextInt(maxXInclination * 2) - maxXInclination) * metrics.density
                    }
                    spriteAnimation.translationXBy(ballXMovement)
                }
                else if(score>=15){
                    ballXMovement=(Random.nextInt(defaultInclination*2)-defaultInclination)*metrics.density
                    spriteAnimation.translationXBy(ballXMovement)

                }


                //Thread che rileva la collisione
                img_falling_sprite.post {
                    Thread {
                        //uso il tag per dire se l'oggetto sta cadendo o no
                        while (img_falling_sprite.tag == true) {
                            if (img_falling_sprite.y + img_falling_sprite.height >= trashBinContainer.y+(trashBinContainer.height/8) && img_falling_sprite.y + img_falling_sprite.height <= trashBinContainer.y + (trashBinContainer.height / 4)) {
                                if ((img_falling_sprite.x >= trashBinContainer.x && img_falling_sprite.x <= trashBinContainer.x + trashBinContainer.width) || (img_falling_sprite.x + img_falling_sprite.width >= trashBinContainer.x && img_falling_sprite.x + img_falling_sprite.width <= trashBinContainer.x + trashBinContainer.width)) {
                                    println("intersezione!!!; img_falling_sprite: ${img_falling_sprite.y + img_falling_sprite.height}")
                                    img_falling_sprite.tag = false
                                    //spawnFallingTrash()
                                }
                            }
                        }
                    }.start()

                }
            }

        }
    }

    fun objectCatched(img_falling_sprite: ImageView){
        img_falling_sprite.clearAnimation()
        img_falling_sprite.animate().setUpdateListener(null)
        score++
        //TODO al posto che punti caricare la stirnga dal file di stringhe (per le possibili traduzioni
        binding.txtScore.text = score.toString() + " punti"

        lottie.playAnimation()

        println(lottie.y)
        img_falling_sprite.animate()
            .translationX(trashBinContainer.x + (trashBinContainer.width / 2) - (img_falling_sprite.width / 2))
            .translationY(trashBinContainer.y + img_falling_sprite.height)
            .alpha(1f)
            .setDuration(245)
            .withEndAction {
                binding.relativeLayout.removeView(img_falling_sprite)
            }
    }
}