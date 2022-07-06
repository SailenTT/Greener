package com.eco.app.games

import android.animation.Animator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.eco.app.R
import com.eco.app.profile.RegisterPage
import com.eco.app.databinding.FragmentGarbageSorterGameBinding
import com.google.android.material.transition.MaterialContainerTransform
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.random.Random


class GarbageSorterFragment : Fragment(), View.OnTouchListener{
    private lateinit var binding : FragmentGarbageSorterGameBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var UID : String
    private lateinit var bestDivideScore : String
    private lateinit var userReference : DatabaseReference
    private var xStart= 0.0F
    private var score=0
    private var gameRunning=false
    private var firstStart=true
    private val defaultSpeed=2250L
    private val minimumSpeed=1850L
    private val spawnDelay=2000L
    private val minimumSpawnDelay=900L
    private lateinit var spawnThread: Thread
    private lateinit var paperBinFrontLayer: ImageView
    private lateinit var plasticBinFrontLayer: ImageView
    private lateinit var organicBinFrontLayer: ImageView
    private lateinit var paperBinTop: ImageView
    private val isFalling="falling_status"
    private val wasteType="waste_type"
    //TODO rendere questa variabile statica e pubblica
    private var numberOfPaperWasteIcons=0
    private var numberOfPlasticWasteIcons=0
    private var numberOfOrganicWasteIcons=0
    private var gameOverScreen: RelativeLayout?=null
    val prePathToPaperIcon: String="paper_waste_"
    val prePathToPlasticIcon: String="plastic_waste_"
    val prePathToOrganicIcon: String="organic_waste_"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedElementEnterTransition = MaterialContainerTransform()
        auth = FirebaseAuth.getInstance()
        database =
            Firebase.database(RegisterPage.PATHTODB)
        binding= FragmentGarbageSorterGameBinding.inflate(layoutInflater,container,false)

        paperBinFrontLayer=binding.paperBinFrontLayer
        plasticBinFrontLayer=binding.plasticBinFrontLayer
        organicBinFrontLayer=binding.organicBinFrontLayer

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
        gameOverScreen?.visibility=View.INVISIBLE
        //Toast.makeText(context,"Gioco Startato",Toast.LENGTH_SHORT).show()
        binding.txtScore.text = 0.toString()
        score=0


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
        spawnThread=Thread{
            try{
                Thread.sleep(200)
                while(gameRunning&&!Thread.currentThread().isInterrupted) {
                    println("spawna il prossimo")

                    //Fai partire il gioco dopo tot millisecondi
                    spawnFallingTrash()

                    var newDelay=spawnDelay-(score*5)
                    if(newDelay<minimumSpawnDelay){
                        newDelay=minimumSpawnDelay
                    }
                    if(!Thread.currentThread().isInterrupted) {
                        Thread.sleep(newDelay)
                    }
                }
            }
            catch(exception: InterruptedException){
                println("spawn trhead interrotto")
            }
        }

        spawnThread.start()
    }


    //Calcola il numero di file dentro la cartella drawable che contengono nel nome il prefisso delle immagini
    fun calculateNumberOfWasteIcons() {
        //TODO quando starta, mettere un controllo se questo ha finito
        Thread{
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
        }.start()

    }

    fun spawnFallingTrash(){
        activity?.runOnUiThread {
            run {
                val img_falling_sprite = ImageView(requireContext())
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

                var ballSize = (metrics.density * 55).toInt()

                img_falling_sprite.y = -ballSize.toFloat()

                if (score >= 100) {
                    img_falling_sprite.y += ((Random.nextInt(200) + 100) * metrics.density)
                }

                /*if(score>=80){
                    ballSize-=ballSize/3
                }
                else*/ if (score >= 60) {
                    ballSize -= (ballSize / 4)
                }
                else if (score >= 40) {
                    ballSize -= (ballSize / 5)
                }
                else if (score >= 25) {
                    ballSize -= (ballSize / 6)
                }

                img_falling_sprite.layoutParams.height = ballSize
                img_falling_sprite.layoutParams.width = ballSize
                img_falling_sprite.x =
                    Random.nextInt((binding.root.width - ballSize))
                        .toFloat()

                var ballXMovement: Float? = null


                val spriteAnimation = img_falling_sprite.animate()
                    .translationY(binding.root.height - (img_falling_sprite.layoutParams.height / 2).toFloat())
                    //.rotationBy(280F)
                    .setDuration(defaultSpeed)
                    //questo si può togliere
                    .withEndAction {
                        gameOver(img_falling_sprite)
                    }
                    .setUpdateListener {
                        //controllo se non sta più cadendo
                        spriteStatusCheck(img_falling_sprite)
                    }

                if (score >= 10) {
                    val currentSpeed = defaultSpeed - (score * 5)
                    if (currentSpeed >= minimumSpeed) {
                        spriteAnimation.duration = currentSpeed
                    } else {
                        spriteAnimation.duration = minimumSpeed
                    }
                }

                //Thread che rileva la collisione
                checkSpriteCollision(img_falling_sprite)
            }
        }
    }


    fun checkSpriteCollision(img_falling_sprite: ImageView){
        Thread {
            //uso il tag per dire se l'oggetto sta cadendo o no
            while ((img_falling_sprite.tag as HashMap<String,Any>)[isFalling] == true) {
                val binY=binding.binsContainer.y+binding.paperBinFrontLayer.y
                //il secondo confronto per l'altezza si può togliere perché cade per forza in un cestino l'oggetto
                if (img_falling_sprite.y + img_falling_sprite.height >= binY+(img_falling_sprite.height/4) && img_falling_sprite.y + img_falling_sprite.height <= binY + img_falling_sprite.height/2 ) {
                    (img_falling_sprite.tag as HashMap<String, Any>)[isFalling] = false
                }
            }
        }.start()
    }

    fun spriteStatusCheck(img_falling_sprite: ImageView){
        if(gameRunning) {
            if ((img_falling_sprite.tag as HashMap<String,Any>)[isFalling]== false) {
                objectCatched(img_falling_sprite)
            }
        }
        else{
            (img_falling_sprite.tag as HashMap<String,Any>)[isFalling]=false
            img_falling_sprite.animate().cancel()
            img_falling_sprite.animate().setUpdateListener(null)
            binding.root.removeView(img_falling_sprite)
        }
    }

    fun objectCatched(img_falling_sprite: ImageView){
        img_falling_sprite.animate().cancel()
        img_falling_sprite.animate().setUpdateListener(null)
        img_falling_sprite.setOnTouchListener(null)

        var correctBin=false

        val wasteId=(img_falling_sprite.tag as HashMap<String,Any>)[wasteType] as Int

        var binId=0

        if ((//img_falling_sprite.x >= paperBinFrontLayer.x &&
                    img_falling_sprite.x+img_falling_sprite.width/3*2 <= paperBinFrontLayer.x + paperBinFrontLayer.width))// || (img_falling_sprite.x + img_falling_sprite.width >= paperBinFrontLayer.x && img_falling_sprite.x + img_falling_sprite.width/3*2 <= paperBinFrontLayer.x + paperBinFrontLayer.width)) {
        {
            binId = 0
        }
        else if ((img_falling_sprite.x+img_falling_sprite.width >= plasticBinFrontLayer.x && img_falling_sprite.x+img_falling_sprite.width/3*2 <= plasticBinFrontLayer.x + plasticBinFrontLayer.width)) //|| (img_falling_sprite.x + img_falling_sprite.width >= plasticBinFrontLayer.x && img_falling_sprite.x + img_falling_sprite.width/3*2 <= plasticBinFrontLayer.x + plasticBinFrontLayer.width)) {
        {
            binId=1
        }
        else //if ((img_falling_sprite.x >= organicBinFrontLayer.x && img_falling_sprite.x <= organicBinFrontLayer.x + organicBinFrontLayer.width) || (img_falling_sprite.x + img_falling_sprite.width/3*2 >= organicBinFrontLayer.x && img_falling_sprite.x + img_falling_sprite.width <= organicBinFrontLayer.x + organicBinFrontLayer.width)) {
        {
            binId=2
        }

        if(wasteId==binId){
            correctBin=true
        }

        var newY=img_falling_sprite.y
        val width=img_falling_sprite.width
        val height=img_falling_sprite.height
        binding.root.removeView(img_falling_sprite)

        var binContainer: RelativeLayout=binding.binsContainer
        var binFrontLayer: ImageView?=null

        when(binId){
            0-> {
                binFrontLayer=paperBinFrontLayer
            }
            1-> {
                binFrontLayer=plasticBinFrontLayer
               }
            2-> {
                binFrontLayer=organicBinFrontLayer
            }
        }

        binContainer.addView(img_falling_sprite)
        newY-=(binContainer.y)
        img_falling_sprite.y=newY
        img_falling_sprite.layoutParams.width=width
        img_falling_sprite.layoutParams.height=height
        img_falling_sprite.requestLayout()

        img_falling_sprite.bringToFront()
        binFrontLayer!!.bringToFront()

        img_falling_sprite.animate()
            .translationY(binFrontLayer.y+binFrontLayer.height/2)
            .translationX(binFrontLayer.x+binFrontLayer.width/2-img_falling_sprite.width/2)
            .setDuration(440)
            .withEndAction {
                if(!correctBin){
                    binContainer.removeView(img_falling_sprite)
                    gameOver(img_falling_sprite)
                }
            }


        if(correctBin){
            score++
            binding.txtScore.text = score.toString()
        }

    }

    fun gameOver(img_falling_sprite: ImageView){
        if(gameRunning) {
            spawnThread.interrupt()
            println("game over")
            //fermo il cestino
            (img_falling_sprite.tag as HashMap<String,Any>)[isFalling] = false
            gameRunning=false
            //salvo il max score nel db
            if(auth.currentUser != null){
                auth.uid?.let { Log.d("okokok", it) }
                UID = auth.uid!!
                userReference = database.getReference("Users")
                userReference.child(UID).child("divide_score").get().addOnSuccessListener { //getto il tuo max score
                    bestDivideScore = it.value.toString()
                    val bestDivideInt = Integer.parseInt(bestDivideScore) //parsing
                    score  = Integer.parseInt(binding.txtScore.text.toString())
                    if(bestDivideInt < score){ //se il tuo max score è piu piccolo, aggiorno il db
                        //Toast.makeText(context, "OK", Toast.LENGTH_SHORT).show()
                        userReference.child(UID).child("divide_score").setValue(score)
                    }
                }
            }
            //faccio comparire la schermata di fine
            img_falling_sprite.setOnTouchListener(null)
            if(gameOverScreen==null) {
                gameOverScreen = binding.gameOverStub.inflate() as RelativeLayout
            }
            else {
                gameOverScreen!!.visibility = View.VISIBLE
            }
            gameOverScreen!!.findViewById<TextView>(R.id.txt_final_score).text = "Hai fatto " + score.toString() + " punti"
            score=0
            gameOverScreen!!.findViewById<ImageButton>(R.id.restart_game_button).setOnClickListener { startGame() }
            binding.root.removeView(img_falling_sprite)
        }
    }
}