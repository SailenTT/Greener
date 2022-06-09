package com.eco.app

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.eco.app.databinding.FragmentGarbageSorterGameBinding
import kotlin.random.Random


class GarbageSorterFragment : Fragment(), View.OnTouchListener{
    private lateinit var binding : FragmentGarbageSorterGameBinding
    private var xStart= 0.0F
    private var score=0
    private var gameRunning=false
    private var firstStart=true
    private val defaultSpeed=2250L
    private val minimumSpeed=1850L
    private val spawnDelay=2000L
    private val minimumSpawnDelay=900L
    private lateinit var paperBinContainer: RelativeLayout
    private lateinit var plasticBinContainer: RelativeLayout
    private lateinit var indiffBinContainer: RelativeLayout
    private lateinit var organicBinContainer: RelativeLayout
    private val isFalling="falling_status"
    private val wasteType="waste_type"
    //TODO rendere questa variabile statica e pubblica
    private var numberOfPaperWasteIcons=0
    private var numberOfPlasticWasteIcons=0
    private var numberOfOrganicWasteIcons=0
    val prePathToPaperIcon: String="paper_waste_"
    val prePathToPlasticIcon: String="plastic_waste_"
    val prePathToOrganicIcon: String="organic_waste_"
    private var last_falling_sprite: ImageView?=null
    private lateinit var trashesContainerLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentGarbageSorterGameBinding.inflate(layoutInflater,container,false)

        paperBinContainer=binding.paperBinContainer
        plasticBinContainer=binding.plasticBinContainer
        //indiffBinContainer=binding.indiffBinContainer
        organicBinContainer=binding.organicBinContainer

        trashesContainerLayout=binding.trashesContainer

        binding.startGameButton.setOnClickListener {
            startGame()
        }

        calculateNumberOfWasteIcons()

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
                    view.x = 0F
                } else if (newX > ((binding.root.width - view.width).toFloat())) {
                    view.x = (binding.root.width - view.width).toFloat()
                } else {
                    view.x = newX
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

        binding.root.removeView(last_falling_sprite)

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
                Thread.sleep(spawnDelay)
            }
        }.start()
    }


    //Calcola il numero di file dentro la cartella drawable che contengono nel nome il prefisso delle immagini
    fun calculateNumberOfWasteIcons() {

        for(i in 0..100){
            val resId=resources.getIdentifier("$prePathToOrganicIcon$i","drawable",requireContext().packageName)
            if(resId!=0){
                numberOfOrganicWasteIcons++
            }
            else{
                break
            }
        }
        for(i in 0..100){
            val resId=resources.getIdentifier("$prePathToPaperIcon$i","drawable",requireContext().packageName)
            if(resId!=0){
                numberOfPaperWasteIcons++
            }
            else{
                break
            }
        }
        for(i in 0..100){
            val resId=resources.getIdentifier("$prePathToPlasticIcon$i","drawable",requireContext().packageName)
            if(resId!=0){
                numberOfPlasticWasteIcons++
            }
            else{
                break
            }
        }
    }

    fun spawnFallingTrash(){
        activity?.runOnUiThread{
            run {
                val img_falling_sprite = ImageView(requireContext())
                last_falling_sprite=img_falling_sprite
                img_falling_sprite.setOnTouchListener(this)

                val metrics = requireContext().resources.displayMetrics

                //estraggo un tipo di rifiuto
                val type=Random.nextInt(3)
                var resId=0
                var imgName=""
                when(type){
                    0->{
                        imgName=prePathToPaperIcon
                        resId=Random.nextInt(numberOfPaperWasteIcons)
                    }
                    1->{
                        imgName=prePathToPlasticIcon
                        resId=Random.nextInt(numberOfPlasticWasteIcons)
                    }
                    2->{
                        imgName=prePathToOrganicIcon
                        resId=Random.nextInt(numberOfOrganicWasteIcons)
                    }
                }

                imgName+=resId

                val uri="@drawable/$imgName"
                val imgRes=resources.getIdentifier(uri,null,requireActivity().packageName)
                img_falling_sprite.setImageDrawable(ResourcesCompat.getDrawable(resources,imgRes,null))

                val tag=HashMap<String,Any>()
                tag[wasteType] = type
                tag[isFalling] = true
                img_falling_sprite.tag=tag

                binding.root.addView(img_falling_sprite)
                trashesContainerLayout.translationZ=100F
                trashesContainerLayout.requestLayout()
                trashesContainerLayout.invalidate()

                var ballSize=(metrics.density * 55).toInt()

                img_falling_sprite.y=0F

                if(score>=100){
                    img_falling_sprite.y+=((Random.nextInt(200)+100)*metrics.density)
                }

                /*if(score>=80){
                    ballSize-=ballSize/3
                }
                else*/ if(score>=60){
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

                //Metto i cestini davanti ai rifiuti


                //Falling tag
                //volendo si può usare il bounce per far riprendere i primi e poi toglierlo

                //non faccio sparire la carta per far capire all'utente che ha inquinato
                var ballXMovement: Float?=null

                val spriteAnimation=img_falling_sprite.animate()
                    .translationY(binding.root.height - (img_falling_sprite.layoutParams.height/2).toFloat())
                    //.rotationBy(280F)
                    .setDuration(defaultSpeed)
                    .withEndAction {
                        if((img_falling_sprite.tag as HashMap<String,Any>)[isFalling]==true&&gameRunning) {
                            println("game over")
                            //fermo il cestino
                            (img_falling_sprite.tag as HashMap<String,Any>)[isFalling] = false
                            gameRunning=false
                            //faccio comparire la schermata di fine
                            img_falling_sprite.setOnTouchListener(null)
                            binding.txtFinalScore.text = "Hai fatto " + score.toString() + " punti"
                            score=0
                            binding.restartGameButton.setOnClickListener { startGame() }
                            binding.gameOverScreen.visibility = View.VISIBLE
                            binding.root.removeView(img_falling_sprite)
                        }
                    }
                spriteAnimation.setUpdateListener {value->
                    //controllo se non sta più cadendo
                    if(gameRunning) {
                        if ((img_falling_sprite.tag as HashMap<String,Any>)[isFalling]== false) {
                            objectCatched(img_falling_sprite)
                        }
                    }
                    else{
                        (img_falling_sprite.tag as HashMap<String,Any>)[isFalling]=false
                        img_falling_sprite.clearAnimation()
                        img_falling_sprite.animate().setUpdateListener(null)
                        binding.root.removeView(img_falling_sprite)
                    }
                }

                if(score>=10){
                    val currentSpeed=defaultSpeed-(score*5)
                    if(currentSpeed>=minimumSpeed) {
                        spriteAnimation.duration = currentSpeed
                    }
                    else{
                        spriteAnimation.duration=minimumSpeed
                    }
                }


                //Thread che rileva la collisione
                img_falling_sprite.post {
                    Thread {
                        //uso il tag per dire se l'oggetto sta cadendo o no
                        while ((img_falling_sprite.tag as HashMap<String,Any>)[isFalling] == true) {
                            val id=(img_falling_sprite.tag as HashMap<String,Any>)[wasteType]
                            //print("y=${img_falling_sprite.y}; height=${img_falling_sprite.height}; linear.y=${binding.linearLayout.y}")
                            if (img_falling_sprite.y + img_falling_sprite.height >= trashesContainerLayout.y+(trashesContainerLayout.height/8) && img_falling_sprite.y + img_falling_sprite.height <= trashesContainerLayout.y + (trashesContainerLayout.height / 4)) {
                                print("id=$id")
                                if ((img_falling_sprite.x >= paperBinContainer.x && img_falling_sprite.x <= paperBinContainer.x + paperBinContainer.width) || (img_falling_sprite.x + img_falling_sprite.width >= paperBinContainer.x && img_falling_sprite.x + img_falling_sprite.width <= paperBinContainer.x + paperBinContainer.width)) {
                                    if(id==0) {
                                        (img_falling_sprite.tag as HashMap<String, Any>)[isFalling] = false
                                    }
                                    //TODO mettere la roba per il rifiuto sbagliato
                                }
                                if ((img_falling_sprite.x >= plasticBinContainer.x && img_falling_sprite.x <= plasticBinContainer.x + plasticBinContainer.width) || (img_falling_sprite.x + img_falling_sprite.width >= plasticBinContainer.x && img_falling_sprite.x + img_falling_sprite.width <= plasticBinContainer.x + plasticBinContainer.width)) {
                                    if(id==1) {
                                        (img_falling_sprite.tag as HashMap<String, Any>)[isFalling] = false
                                    }
                                }
                                if ((img_falling_sprite.x >= organicBinContainer.x && img_falling_sprite.x <= organicBinContainer.x + organicBinContainer.width) || (img_falling_sprite.x + img_falling_sprite.width >= organicBinContainer.x && img_falling_sprite.x + img_falling_sprite.width <= organicBinContainer.x + organicBinContainer.width)) {
                                    if(id==2) {
                                        (img_falling_sprite.tag as HashMap<String, Any>)[isFalling] = false
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
        val animation=img_falling_sprite.animate()
        img_falling_sprite.clearAnimation()
        animation.setUpdateListener(null)
        score++
        binding.txtScore.text = score.toString()

        //lottie.playAnimation()

        animation
            .alpha(1f)
            .setDuration(250)
            .translationY(trashesContainerLayout.y + img_falling_sprite.height)
            .withEndAction {
                binding.root.removeView(img_falling_sprite)
            }

        when((img_falling_sprite.tag as HashMap<String,Any>)[wasteType]){
            0->{
                binding.paperTrashIcon.animate()
                    .rotation(360F)
                    .setDuration(450)
                    .start()

                animation
                    .translationX(paperBinContainer.x + (paperBinContainer.width / 2) - (img_falling_sprite.width / 2))
            }
            1->{
                binding.plasticTrashIcon.animate()
                    .rotation(360F)
                    .setDuration(450)
                    .start()

                animation
                    .translationX(plasticBinContainer.x + (plasticBinContainer.width / 2) - (img_falling_sprite.width / 2))
            }
            2->{
                binding.organicTrashIcon.animate()
                    .rotation(360F)
                    .setDuration(450)
                    .start()

                animation
                    .translationX(organicBinContainer.x + (organicBinContainer.width / 2) - (img_falling_sprite.width / 2))
            }
        }

        animation.start()
    }
}