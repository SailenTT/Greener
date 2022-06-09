package com.eco.app

import android.animation.Animator
import android.os.Bundle
import android.view.*
import android.view.animation.BounceInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.eco.app.databinding.FragmentTrashBinGameBinding
import kotlin.random.Random


/**
 * A simple [Fragment] subclass.
 * Use the [TrashBinGameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TrashBinGameFragment : Fragment(), View.OnTouchListener {
    private lateinit var binding: FragmentTrashBinGameBinding
    private var xStart= 0.0F
    private lateinit var trashBinContainer: RelativeLayout
    private var score=0
    private var last_falling_sprite: ImageView?=null
    private lateinit var lottieRecycleAnimation: LottieAnimationView
    private val defaultSpeed=2250L
    private val minimumSpeed=1400L
    private val defaultInclination=300
    private val maxXInclination=800
    private var firstStart=true
    private var gameRunning=false
    private val spawnDelay=1000L
    private val minimumSpawnDelay=650L
    private lateinit var trashBinBottomLayer: ImageView
    private lateinit var trashBinTopLayer: ImageView
    //TODO aggiungere la possibilità di mettere in pausa il gioco

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        //Inflate the binding for this fragment
        binding= FragmentTrashBinGameBinding.inflate(inflater,container,false)

        trashBinContainer=binding.trashBinContainer
        trashBinBottomLayer=binding.trashBin
        trashBinTopLayer=binding.trashBinTopLayer

        binding.startGameButton.setOnClickListener {
            startGame()
        }

        lottieRecycleAnimation=binding.lottie

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
                val containerBorderSize=(trashBinContainer.width-trashBinBottomLayer.width)/2

                if (newX+containerBorderSize < 0F) {
                    trashBinContainer.x = 0F-containerBorderSize
                } else if (newX-containerBorderSize > ((binding.root.width - trashBinContainer.width).toFloat())) {
                    trashBinContainer.x = (binding.root.width - trashBinContainer.width).toFloat()+containerBorderSize
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
                var newDelay=spawnDelay-(score*5)
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

                binding.root.addView(img_falling_sprite)
                //porto il cestino in primo piano per fare in modo che le palline cadano dentro

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

                img_falling_sprite.y=0-ballSize.toFloat()

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
                            binding.root.removeView(img_falling_sprite)
                            //TODO se l'utente non è loggato far comparire il tasto per loggare
                        }
                        else{
                            binding.root.removeView(img_falling_sprite)
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
                        img_falling_sprite.tag=false
                        img_falling_sprite.clearAnimation()
                        img_falling_sprite.animate().setUpdateListener(null)
                        binding.root.removeView(img_falling_sprite)
                    }
                }

                if(score>=10){
                    val currentSpeed=defaultSpeed-(score*9)
                    if(currentSpeed>=minimumSpeed) {
                        spriteAnimation.duration = currentSpeed
                    }
                    else{
                        spriteAnimation.duration=minimumSpeed
                    }
                }

                if(score>=25){
                    if(defaultInclination+(score*5)>maxXInclination) {
                        ballXMovement =
                            (Random.nextInt(maxXInclination * 2) - maxXInclination) * metrics.density                    }
                    else{
                        ballXMovement =
                            (Random.nextInt((defaultInclination+(score*5)) * 2) - (defaultInclination+(score*5))) * metrics.density
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
                        binding.root.bringChildToFront(trashBinContainer)
                        //uso il tag per dire se l'oggetto sta cadendo o no
                        while (img_falling_sprite.tag == true) {
                            val binY=trashBinTopLayer.y
                            val binX=trashBinContainer.x+((trashBinContainer.width-trashBinBottomLayer.width)/2).toFloat()
                            //TODO mettere che se il rifiuto cade oltre la metà del bordo del cestino, rimbalza in alto
                            if (img_falling_sprite.y + img_falling_sprite.height >=binY && img_falling_sprite.y + img_falling_sprite.height <= binY+ (trashBinBottomLayer.height / 2)) {
                                if ((img_falling_sprite.x >= binX && img_falling_sprite.x <= binX + trashBinTopLayer.width) || (img_falling_sprite.x + img_falling_sprite.width >= binX && img_falling_sprite.x + img_falling_sprite.width <= binX + trashBinTopLayer.width)) {
                                    //controllo se la pallina è caduta bene o in caso contrario, la faccio rimbalzare di nuovo
                                    if(img_falling_sprite.x+(img_falling_sprite.width/3*2)<=binX||img_falling_sprite.x+(img_falling_sprite.width/3)>=binX+trashBinBottomLayer.width){
                                        img_falling_sprite.animate().interpolator=BounceInterpolator()
                                    }
                                    else {
                                        img_falling_sprite.tag = false
                                    }
                                }
                            }
                        }
                    }.start()

                }
            }

        }
    }

    fun objectCatched(img_falling_sprite: ImageView) {
        img_falling_sprite.clearAnimation()
        img_falling_sprite.animate().setUpdateListener(null)
        score++
        binding.txtScore.text = score.toString()


        //trashBinTopContainer.bringChildToFront(img_falling_sprite)
        //trashBinTopContainer.bringChildToFront(binding.voidView)

        lottieRecycleAnimation.playAnimation()

        val containerHeight = trashBinContainer.height+img_falling_sprite.height
        binding.root.removeView(img_falling_sprite)
        trashBinContainer.addView(img_falling_sprite)
        val x = img_falling_sprite.x - trashBinContainer.x
        val y=img_falling_sprite.y
        val height = img_falling_sprite.height
        val width = img_falling_sprite.width
        img_falling_sprite.layoutParams.height = height
        img_falling_sprite.layoutParams.width = width
        img_falling_sprite.x = x
        img_falling_sprite.y = y
        img_falling_sprite.invalidate()
        trashBinContainer.invalidate()
        trashBinContainer.layoutParams.height = containerHeight
        trashBinContainer.bringChildToFront(trashBinBottomLayer)
        trashBinContainer.bringChildToFront(lottieRecycleAnimation)

        img_falling_sprite.animate()
            .translationX((trashBinContainer.width / 2) - (img_falling_sprite.width / 2).toFloat())
            //.translationY(trashBinContainer.height/2.toFloat())
            .translationY(trashBinTopLayer.y + trashBinTopLayer.height.toFloat())
                //dimezzo la dimensione del rifiuto
            .setDuration(350)
            .withEndAction {
                trashBinContainer.removeView(img_falling_sprite)
            }
    }
}