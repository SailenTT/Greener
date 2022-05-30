package com.eco.app

import android.animation.Animator
import android.content.res.AssetManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.eco.app.databinding.FragmentGarbageSorterGameBinding
import java.util.*
import kotlin.collections.HashMap
import kotlin.random.Random

class GarbageSorterFragment : Fragment(), View.OnTouchListener{
    private lateinit var binding : FragmentGarbageSorterGameBinding
    private var xStart= 0.0F
    private var score=0
    private var gameRunning=false
    private var firstStart=true
    private val defaultSpeed=2700L
    private val minimumSpeed=900L
    private val spawnDelay=1400L
    private val minimumSpawnDelay=900L
    private lateinit var paperBinContainer: RelativeLayout
    private lateinit var plasticBinContainer: RelativeLayout
    private lateinit var indiffBinContainer: RelativeLayout
    private lateinit var organicBinContainer: RelativeLayout
    private val isFalling="falling_status"
    private val wasteType="waste_type"
    //TODO rendere questa variabile statiche e pubbliche
    val prePathToAsset: String="/waste_type_"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentGarbageSorterGameBinding.inflate(layoutInflater,container,false)

        paperBinContainer=binding.paperBinContainer
        plasticBinContainer=binding.plasticBinContainer
        indiffBinContainer=binding.indiffBinContainer
        organicBinContainer=binding.organicBinContainer

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

                val newX = view?.x?.plus((motion.x-xStart))

                if (newX!! < 0F) {
                    view?.x = 0F
                } else if (newX!! > ((binding.root.width - view?.width!!).toFloat())) {
                    view?.x = (binding.root.width - view?.width).toFloat()
                } else {
                    view?.x = newX
                }
                return true
            }
        }
        return true
    }

    fun startGame() {

        //binding.startGameInstructionsContainer.visibility=View.INVISIBLE
        //binding.gameOverScreen.visibility = View.INVISIBLE
        //Toast.makeText(context,"Gioco Startato",Toast.LENGTH_SHORT).show()
        binding.txtScore.text = 0.toString()
        score=0

        //binding.root.removeView(last_falling_sprite)

        /*val layoutParams=(trashBinContainer.layoutParams as RelativeLayout.LayoutParams)
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
        trashBinContainer.layoutParams=layoutParams*/

        gameRunning=true

        if(firstStart) {
            val lottieSwipeAnimation=binding.lottieSwipeAnimation
            lottieSwipeAnimation.visibility=View.VISIBLE

            lottieSwipeAnimation.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator?) {
                    binding.mainContainer.alpha = 0.70F
                    lottieSwipeAnimation.alpha = 1F
                }

                override fun onAnimationCancel(p0: Animator?) {}

                override fun onAnimationRepeat(p0: Animator?) {}

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
                img_falling_sprite.setOnTouchListener(this)

                val metrics = requireContext().resources.displayMetrics

                //estraggo un tipo di rifiuto
                val type=Random.nextInt(4)

                //prendo la lista dei contenuti nella subdirectory relativa al numero di rifiuto estratto
                val assetList=context?.assets?.list(prePathToAsset+type)
                //estraggo l'id dell'immagine da usare
                val resId=Random.nextInt(assetList!!.size)

                //setto l'immagine
                img_falling_sprite.setImageURI(Uri.parse(assetList[resId]))

                val tag=HashMap<String,Any>()
                tag[wasteType] = type
                tag[isFalling] = true
                img_falling_sprite.tag=tag

                binding.root.addView(img_falling_sprite)

                /*var ballSize=(metrics.density * 55).toInt()

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
                var ballXMovement: Float?=null*/

                val spriteAnimation=img_falling_sprite.animate()
                    .translationY(binding.root.height - (img_falling_sprite.layoutParams.height/2).toFloat())
                    .rotationBy(280F)
                    .setDuration(defaultSpeed)
                    .withEndAction {
                        if(img_falling_sprite.tag==true&&gameRunning) {
                            println("game over")
                            //fermo il cestino
                            (img_falling_sprite.tag as HashMap<String,Any>)[isFalling] = false
                            gameRunning=false
                            //faccio comparire la schermata di fine
                            img_falling_sprite.setOnTouchListener(null)
                            //binding.txtFinalScore.text = "Hai fatto " + score.toString() + " punti"
                            score=0
                            //binding.restartGameButton.setOnClickListener { startGame() }
                            //binding.gameOverScreen.visibility = View.VISIBLE
                        }
                    }
                spriteAnimation.setUpdateListener {value->
                    //controllo se non sta più cadendo
                    if(!gameRunning) {
                        img_falling_sprite.clearAnimation()
                        img_falling_sprite.animate().setUpdateListener(null)
                        binding.root.removeView(img_falling_sprite)
                    }
                }

                /*if(score>=10){
                    val currentSpeed=defaultSpeed-(score*18)
                    if(currentSpeed>=minimumSpeed) {
                        spriteAnimation.duration = currentSpeed
                    }
                    else{
                        spriteAnimation.duration=minimumSpeed
                    }
                }

                if(score>=25){

                }
                else if(score>=15){


                }*/


                //Thread che rileva la collisione
                img_falling_sprite.post {
                    Thread {
                        //uso il tag per dire se l'oggetto sta cadendo o no
                        while (img_falling_sprite.tag == true) {
                            val id=(img_falling_sprite.tag as HashMap<String,Any>)[wasteType]
                            if (img_falling_sprite.y + img_falling_sprite.height >= paperBinContainer.y+(paperBinContainer.height/8) && img_falling_sprite.y + img_falling_sprite.height <= paperBinContainer.y + (paperBinContainer.height / 4)) {
                                when(id) {
                                    0->{
                                        if ((img_falling_sprite.x >= paperBinContainer.x && img_falling_sprite.x <= paperBinContainer.x + paperBinContainer.width) || (img_falling_sprite.x + img_falling_sprite.width >= paperBinContainer.x && img_falling_sprite.x + img_falling_sprite.width <= paperBinContainer.x + paperBinContainer.width)) {
                                            (img_falling_sprite.tag as HashMap<String, Any>)[isFalling] = false
                                        }

                                    }
                                    1-> {
                                        if ((img_falling_sprite.x >= plasticBinContainer.x && img_falling_sprite.x <= plasticBinContainer.x + plasticBinContainer.width) || (img_falling_sprite.x + img_falling_sprite.width >= plasticBinContainer.x && img_falling_sprite.x + img_falling_sprite.width <= plasticBinContainer.x + plasticBinContainer.width)) {
                                            (img_falling_sprite.tag as HashMap<String, Any>)[isFalling] = false
                                        }
                                    }
                                    2-> {
                                        if ((img_falling_sprite.x >= indiffBinContainer.x && img_falling_sprite.x <= indiffBinContainer.x + indiffBinContainer.width) || (img_falling_sprite.x + img_falling_sprite.width >= indiffBinContainer.x && img_falling_sprite.x + img_falling_sprite.width <= indiffBinContainer.x + indiffBinContainer.width)) {
                                            (img_falling_sprite.tag as HashMap<String, Any>)[isFalling] = false
                                        }
                                    }
                                    3-> {
                                        if ((img_falling_sprite.x >= organicBinContainer.x && img_falling_sprite.x <= organicBinContainer.x + organicBinContainer.width) || (img_falling_sprite.x + img_falling_sprite.width >= organicBinContainer.x && img_falling_sprite.x + img_falling_sprite.width <= organicBinContainer.x + organicBinContainer.width)) {
                                            (img_falling_sprite.tag as HashMap<String, Any>)[isFalling] = false
                                        }
                                    }
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
        binding.txtScore.text = score.toString() + getString(R.string.points)


        //lottie.playAnimation()

        img_falling_sprite.animate()
                //TODO sostituire trashBinContainer col cestino giusto
            //.translationX(trashBinContainer.x + (trashBinContainer.width / 2) - (img_falling_sprite.width / 2))
            //.translationY(trashBinContainer.y + img_falling_sprite.height)
            .alpha(1f)
            .setDuration(245)
            .withEndAction {
                binding.root.removeView(img_falling_sprite)
            }
    }
}