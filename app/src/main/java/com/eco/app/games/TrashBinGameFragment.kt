package com.eco.app.games

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.eco.app.R
import com.eco.app.databinding.FragmentTrashBinGameBinding
import com.google.android.material.transition.MaterialContainerTransform
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.math.abs
import kotlin.random.Random


/**
 * A simple [Fragment] subclass.
 * Use the [TrashBinGameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TrashBinGameFragment : Fragment(), View.OnTouchListener {
    companion object {
        const val isFalling="isFalling"
        const val horizontalDirection="ballXMovement"
    }
    private lateinit var binding: FragmentTrashBinGameBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var UID : String
    private lateinit var bestTrashScore : String
    private lateinit var userReference : DatabaseReference
    private var xStart= 0.0F
    private lateinit var trashBinContainer: RelativeLayout
    private var score=0
    private lateinit var lottieRecycleAnimation: LottieAnimationView
    private val defaultSpeed=2100L
    private val minimumSpeed=1300L
    private val defaultInclination=300
    private val maxXInclination=900
    private var firstStart=true
    private var gameRunning=false
    private val spawnDelay=1000L
    private val minimumSpawnDelay=650L
    private lateinit var spawnThread: Thread;
    private lateinit var trashBinFrontLayer: ImageView
    private lateinit var trashBinBackLayer: ImageView
    private var gameOverScreen: RelativeLayout?=null
    //TODO aggiungere la possibilità di mettere in pausa il gioco

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        sharedElementEnterTransition = MaterialContainerTransform()
        //Inflate the binding for this fragment
        binding= FragmentTrashBinGameBinding.inflate(inflater,container,false)
        auth = FirebaseAuth.getInstance()
        database =
            Firebase.database(getString(R.string.path_to_db))
        trashBinContainer=binding.trashBinContainer
        trashBinFrontLayer=binding.trashBinFrontLayer
        trashBinBackLayer=binding.trashBinBackLayer

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
                val containerBorderSize=(trashBinContainer.width-trashBinFrontLayer.width)/2

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
        gameOverScreen?.visibility=View.INVISIBLE
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

                override fun onAnimationCancel(p0: Animator?) { }

                override fun onAnimationRepeat(p0: Animator?) { }

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
                while(gameRunning&&!Thread.currentThread().isInterrupted){
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
            catch (exception: InterruptedException){
                println("Spawn thread interrotto")
            }
        }
        spawnThread.start()
    }

    fun spawnFallingTrash(){
        activity?.runOnUiThread{
            run {
                val img_falling_sprite = ImageView(requireContext())

                val metrics = requireContext().resources.displayMetrics

                img_falling_sprite.setImageResource(R.drawable.crumbled_paper)

                val map=HashMap<String,Any>()
                map[isFalling] = true

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

                var ballXMovement: Float=0F

                val spriteAnimation=img_falling_sprite.animate()
                    .translationY(binding.root.height - (img_falling_sprite.layoutParams.height/2).toFloat())
                    .rotationBy(280F)
                    .setDuration(defaultSpeed)
                    .withEndAction {
                        if((img_falling_sprite.tag as HashMap<String,Any>)[isFalling]==true&&gameRunning) {
                            spriteFallen(img_falling_sprite)
                        }
                        else{
                            binding.root.removeView(img_falling_sprite);
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

                map[horizontalDirection] = ballXMovement

                spriteAnimation.setUpdateListener {
                    //controllo se non sta più cadendo
                    spriteStatusCheck(img_falling_sprite,null)
                }

                img_falling_sprite.tag=map

                checkSpriteCollision(img_falling_sprite)
            }

        }
    }

    //Thread che rileva la collisione
    fun checkSpriteCollision(img_falling_sprite: ImageView){
        Thread {
            //uso il tag per dire se l'oggetto sta cadendo o no
            while ((img_falling_sprite.tag as HashMap<String,Any>)[isFalling] == true) {
                val binY=trashBinBackLayer.y
                val binX=trashBinContainer.x+((trashBinContainer.width-trashBinFrontLayer.width)/2).toFloat()
                if (img_falling_sprite.y + img_falling_sprite.height >=binY+img_falling_sprite.height/6 && img_falling_sprite.y + img_falling_sprite.height <= binY+ (trashBinFrontLayer.height / 2)) {
                    if ((img_falling_sprite.x >= binX && img_falling_sprite.x <= binX + trashBinBackLayer.width) || (img_falling_sprite.x + img_falling_sprite.width >= binX && img_falling_sprite.x + img_falling_sprite.width <= binX + trashBinBackLayer.width)) {
                        //controllo se la pallina è caduta bene o in caso contrario, la faccio rimbalzare di nuovo
                        (img_falling_sprite.tag as HashMap<String,Any>)[isFalling] = false
                    }
                }
            }
        }.start()
    }

    //Fa rimbalzare la pallina se è caduta a bordo del cestino
    fun spriteBounce(img_falling_sprite: ImageView){
        var currentSpeed=defaultSpeed-(score*9)
        if(currentSpeed<minimumSpeed) {
            currentSpeed=minimumSpeed
        }

        var ballXMovement=(img_falling_sprite.tag as HashMap<String,Any>)[horizontalDirection] as Float
        var newX: Float=img_falling_sprite.x

        if(img_falling_sprite.x<trashBinBackLayer.x){
            ballXMovement=-(abs(ballXMovement))
        }
        else{
            ballXMovement=abs(ballXMovement)
        }

        newX+=ballXMovement

        (img_falling_sprite.tag as HashMap<String,Any>)[horizontalDirection]=ballXMovement


        if(newX+img_falling_sprite.width>binding.root.width){
            newX=binding.root.width-img_falling_sprite.width.toFloat()
        }
        else if(newX<0){
            newX=0F
        }

        println("ballXMovement: $ballXMovement")

        var animSet=AnimatorSet()

        val anim1=ObjectAnimator.ofFloat(img_falling_sprite, "translationY",binding.root.height/4F)
        anim1.duration=currentSpeed/2
        val anim2=ObjectAnimator.ofFloat(img_falling_sprite, "translationY",binding.root.height - (img_falling_sprite.layoutParams.height/2).toFloat())
        val xMovement=ObjectAnimator.ofFloat(img_falling_sprite, "translationX",newX)
        xMovement.duration=currentSpeed/2
        anim2.duration=currentSpeed/2

        animSet.doOnEnd {
            checkSpriteCollision(img_falling_sprite)
            anim2.start()
        }

        anim2.addUpdateListener {
            spriteStatusCheck(img_falling_sprite,anim2)
        }
        anim2.doOnEnd {
            spriteFallen(img_falling_sprite)
        }

        animSet.playTogether(anim1,xMovement)
        animSet.start()

    }

    fun objectCatched(img_falling_sprite: ImageView){
        img_falling_sprite.animate().setUpdateListener(null)
        img_falling_sprite.animate().cancel()

        val binX=trashBinContainer.x+((trashBinContainer.width-trashBinFrontLayer.width)/2).toFloat()
        if(img_falling_sprite.x+(img_falling_sprite.width/2+img_falling_sprite.width/8)<=binX||img_falling_sprite.x+(img_falling_sprite.width/2+img_falling_sprite.width/8)>=binX+trashBinBackLayer.width) {
            var ballXMovement=(img_falling_sprite.tag as HashMap<String,Any>)[horizontalDirection] as Float
            ballXMovement=-ballXMovement
            (img_falling_sprite.tag as HashMap<String,Any>)[horizontalDirection]=ballXMovement

            (img_falling_sprite.tag as HashMap<String,Any>)[isFalling]=true

            spriteBounce(img_falling_sprite)
        }
        else {
            score++
            binding.txtScore.text = score.toString()

            lottieRecycleAnimation.playAnimation()

            val containerHeight = trashBinContainer.height + img_falling_sprite.height
            binding.root.removeView(img_falling_sprite)
            //binding.root.removeView(img_falling_sprite)
            trashBinContainer.addView(img_falling_sprite)
            val x = img_falling_sprite.x - trashBinContainer.x
            val y = img_falling_sprite.y
            val height = img_falling_sprite.height
            val width = img_falling_sprite.width
            img_falling_sprite.layoutParams.height = height
            img_falling_sprite.layoutParams.width = width
            img_falling_sprite.x = x
            img_falling_sprite.y = y
            img_falling_sprite.invalidate()
            trashBinContainer.invalidate()
            trashBinContainer.layoutParams.height = containerHeight
            trashBinContainer.bringChildToFront(trashBinFrontLayer)
            trashBinContainer.bringChildToFront(lottieRecycleAnimation)


            img_falling_sprite.animate()
                .translationX((trashBinContainer.width / 2) - (img_falling_sprite.width / 2).toFloat())
                //.translationY(trashBinContainer.height/2.toFloat())
                .translationY(trashBinFrontLayer.y)
                //dimezzo la dimensione del rifiuto
                .setDuration(350)
                .withEndAction {
                    trashBinContainer.removeView(img_falling_sprite)
                }
        }
    }

    fun spriteStatusCheck(img_falling_sprite: ImageView, animation: ObjectAnimator?){
        if(gameRunning) {
            if ((img_falling_sprite.tag as HashMap<String,Any>)[isFalling] == false) {
                //animation?.removeAllUpdateListeners()
                //animation?.doOnEnd {  }
                //TODO sistemare il fatto che non si possa cancellare
                animation?.pause()
                //animation?.cancel()
                objectCatched(img_falling_sprite)
            }
            else if ((img_falling_sprite.x + img_falling_sprite.width) >= binding.root.width) {
                img_falling_sprite.x = (binding.root.width - img_falling_sprite.width).toFloat()

                var ballXMovement=(img_falling_sprite.tag as HashMap<String,Any>)[horizontalDirection] as Float
                ballXMovement=-ballXMovement
                (img_falling_sprite.tag as HashMap<String,Any>)[horizontalDirection]=ballXMovement
                img_falling_sprite.animate().translationX(ballXMovement)

            } else if (img_falling_sprite.x <= 0) {
                var ballXMovement=(img_falling_sprite.tag as HashMap<String,Any>)[horizontalDirection] as Float
                ballXMovement=-ballXMovement
                (img_falling_sprite.tag as HashMap<String,Any>)[horizontalDirection]=ballXMovement

                img_falling_sprite.animate().translationX(ballXMovement)
            }
        }
        else{
            (img_falling_sprite.tag as HashMap<String,Any>)[isFalling]=false
            img_falling_sprite.animate().setUpdateListener(null)
            img_falling_sprite.animate().cancel()
            binding.root.removeView(img_falling_sprite)
        }
    }

    fun spriteFallen(img_falling_sprite: ImageView){
        if((img_falling_sprite.tag as HashMap<String,Any>)[isFalling]==true&&gameRunning) {
            spawnThread.interrupt()
            println("game over")
            //fermo il cestino
            gameRunning=false
            //salvo il max score nel db
            if(auth.currentUser != null){
                auth.uid?.let { Log.d("okokok", it) }
                UID = auth.uid!!
                userReference = database.getReference("Users")
                userReference.child(UID).child("bin_score").get().addOnSuccessListener { //getto il tuo max score
                    bestTrashScore = it.value.toString()
                    val bestTrashInt = Integer.parseInt(bestTrashScore) //parsing
                    score  = Integer.parseInt(binding.txtScore.text.toString())
                    if(bestTrashInt < score){ //se il tuo max score è piu piccolo, aggiorno il db
                        Toast.makeText(context, "OK", Toast.LENGTH_SHORT).show()
                        userReference.child(UID).child("bin_score").setValue(score)
                    }
                }
            }
            (img_falling_sprite.tag as HashMap<String,Any>)[isFalling]=false
            //faccio comparire la schermata di fine
            trashBinContainer.setOnTouchListener(null)
            if(gameOverScreen==null) {
                gameOverScreen = binding.gameOverStub.inflate() as RelativeLayout
            }
            else {
                gameOverScreen!!.visibility = View.VISIBLE
            }
            gameOverScreen!!.findViewById<TextView>(R.id.txt_final_score).text= "Hai fatto " + score.toString() + " punti"
            //binding.txtFinalScore.text = "Hai fatto " + score.toString() + " punti"
            score=0
            gameOverScreen!!.findViewById<ImageButton>(R.id.restart_game_button).setOnClickListener { startGame() }
            //binding.restartGameButton.setOnClickListener { startGame() }
            //binding.gameOverScreen.visibility = View.VISIBLE
            binding.root.removeView(img_falling_sprite)
            //TODO se l'utente non è loggato far comparire il tasto per loggare
        }
        else {
            binding.root.removeView(img_falling_sprite)
        }
    }

    //quando la partita è ancora in corso e l'utente torna indietro, fermo il gioco e lo spawn thread
    override fun onPause() {
        super.onPause()

        if(gameRunning){
            gameRunning=false
            spawnThread.interrupt()
        }
    }
}