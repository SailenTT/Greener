package com.eco.app.games

import android.Manifest
import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.eco.app.R
import com.eco.app.databinding.FragmentGrowingTreeBinding
import com.google.android.material.transition.MaterialContainerTransform
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

class GrowingTreeFragment : Fragment() {
    private lateinit var binding: FragmentGrowingTreeBinding
    private lateinit var wateringCan: LottieAnimationView
    private lateinit var treeImg : ImageView
    private var startX: Float = 0F
    private var startY: Float = 0F
    private var totalSteps : Long=0
    private val canMinFrame=23
    private var stepsUpdated=false
    private val fruitsMaxResIndex=5
    private val fruitMaxNumber=9
    private val fruitMinNumber=5
    private val fruitsList= mutableListOf<ImageView>()
    private val fruitHeight=71
    private val fruitWidth=58
    private lateinit var fruitsShPrefs: SharedPreferences
    private var shop: RelativeLayout?=null
    private lateinit var wateringCanText:TextView
    private lateinit var wateringCanBox: RelativeLayout
    private lateinit var moneyCount:TextView
    private var waterLiters: Int=0
    private var fruitPoints: Long=0
    private var dbScoreReference:DatabaseReference?=null
    private lateinit var shopItemsList: List<String>
    companion object{
        const val treeImgPrefix="growing_tree_frame_"
        val stepsLevels= listOf(0L,5000L,10000L,15000L,20000L,25000L,30000L,35000L,40000L,45000L,50000L,55000L,60000L,65000L)
        const val fruitResId="@drawable/tree_fruit_"
    }

    //TODO salvare il progresso di questo gioco nel db di firebase
    //TODO mettere il punteggio del gioco nel db di firebase
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedElementEnterTransition = MaterialContainerTransform()

        binding=FragmentGrowingTreeBinding.inflate(inflater,container,false)

        shopItemsList=listOf(getString(R.string.shop_item_0))

        fruitsShPrefs=requireContext().getSharedPreferences("growingTreeFruits",Context.MODE_PRIVATE)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //requireContext().getSharedPreferences("trackingPrefs",Context.MODE_PRIVATE).edit().remove("steps").commit()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { //richiesta permesso
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                101
            )
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACTIVITY_RECOGNITION)
            != PackageManager.PERMISSION_GRANTED) {
            //TODO dire all'utente che non può giocare al gioco
            Toast.makeText(requireContext(), "Permission not granted for the tracking", Toast.LENGTH_SHORT).show()
        }

        wateringCanText=binding.wateringCanText

        wateringCan=binding.wateringCan

        wateringCan.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?) {
                if(waterLiters>0) {
                    wateringCan.setMinFrame(canMinFrame)
                    wateringCan.progress = 0F
                    wateringCan.playAnimation()
                }
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationStart(animation: Animator?) {}
        })

        wateringCanBox=binding.wateringCanBox

        wateringCanBox.setOnTouchListener { v, event ->touchListener(v,event)  }

        binding.shopIcon.setOnClickListener {
            //TODO aggiungere animazione di comparsa shop
            if(shop!=null){
                shop?.visibility=View.VISIBLE
            }else{
                shop=binding.shopStub.inflate() as RelativeLayout
            }
            shop?.findViewById<ImageView>(R.id.img_close_shop)?.setOnClickListener {
                shop?.visibility=View.GONE
            }

            shop?.bringToFront()
        }

        moneyCount=binding.moneyCount

        //controllo se l'utente è loggato
        if(Firebase.auth.currentUser!=null){
            dbScoreReference=Firebase.database(getString(R.string.path_to_db))
                .getReference("Users")
                .child(Firebase.auth.currentUser!!.uid)
                .child("growing_tree")

            dbScoreReference?.get()?.addOnSuccessListener {dataSnapshot->
                fruitPoints=dataSnapshot.value as Long
                moneyCount.text=fruitPoints.toString()
            }
        }

        startStepService()

        treeImg=binding.treeImageView

        setTree()

    }

    fun setTree(){
        val sharedPref=requireContext().getSharedPreferences("trackingPrefs",Context.MODE_PRIVATE)
        totalSteps= sharedPref.getLong("steps",0L)

        if(totalSteps==0L){
            sharedPref.edit().putInt("waterLiters",50).apply()
            waterLiters=50
        }
        else{
            waterLiters=sharedPref.getInt("waterLiters",0)
        }
        wateringCanText.text="${waterLiters}L"

        var tree_frame= 0

        if(totalSteps>= stepsLevels[stepsLevels.size-1]){
           tree_frame= stepsLevels.size-1
            totalSteps=stepsLevels[stepsLevels.size-2]
            requireContext().getSharedPreferences("trackingPrefs",Context.MODE_PRIVATE).edit()
                .putLong("steps",totalSteps).commit()

            spawnFruits()

            treeImg.setOnClickListener {
                for(fruit in fruitsList){
                    val newX=binding.fruitsContainer.x+fruit.x
                    val newY=binding.fruitsContainer.y+fruit.y
                    binding.fruitsContainer.removeView(fruit)
                    binding.root.addView(fruit)
                    fruit.x=newX
                    fruit.y=newY
                    fruit.animate()
                        .translationY(binding.root.height.toFloat())
                        .setDuration(1000)
                        .withEndAction {
                            binding.root.removeView(fruit)
                            fruitsList.remove(fruit)
                            fruitsShPrefs.edit()
                                .remove("fruit${fruitsList.indexOf(fruit)}X")
                                .remove("fruit${fruitsList.indexOf(fruit)}Y")
                                .remove("fruit${fruitsList.indexOf(fruit)}W")
                                .remove("fruit${fruitsList.indexOf(fruit)}H")
                                .remove("fruit${fruitsList.indexOf(fruit)}Img")
                                .apply()

                            fruitPoints++

                            if(fruitsList.indexOf(fruit)==fruitsList.size-1){
                                dbScoreReference?.setValue(fruitPoints)
                            }
                        }
                }

                treeImg.setOnClickListener(null)
            }

        }
        else{
            while(!(totalSteps>= stepsLevels[tree_frame]&&totalSteps<= stepsLevels[tree_frame+1])){
                tree_frame++
            }
        }

        var uri="@drawable/$treeImgPrefix$tree_frame"
        var imgRes=resources.getIdentifier(uri,null,requireActivity().packageName)
        if(imgRes==0){
            uri="@drawable/$treeImgPrefix${stepsLevels.size-1}"
            imgRes=resources.getIdentifier(uri,null,requireActivity().packageName)
        }
        val newImg=ResourcesCompat.getDrawable(resources,imgRes,null)
        treeImg.setImageDrawable(newImg)

    }

    private fun startStepService() {
        val intent = Intent(context, StepService::class.java)
        activity!!.startService(intent)
    }

    fun touchListener(v: View, event: MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN->{
                startX = event.x
                startY = event.y
                return true
            }
            MotionEvent.ACTION_MOVE->{
                val deltaX = event.x - startX
                val deltaY = event.y - startY
                v.x += deltaX
                v.y += deltaY
                if (wateringCanBox.x+wateringCan.x >= (binding.wateringCanContainer.x + binding.wateringCanContainer.width)
                    ||wateringCanBox.y+wateringCan.y>= (binding.wateringCanContainer.y + binding.wateringCanContainer.height)
                    ||wateringCanBox.y+wateringCan.y+wateringCan.height<=binding.wateringCanContainer.y){

                    binding.wateringCanText.visibility=View.VISIBLE

                    if (!wateringCan.isAnimating&&waterLiters>0) {
                        val handler=Handler(Looper.getMainLooper())
                        var sleepTime=0L

                        wateringCan.playAnimation()
                        wateringCan.addAnimatorUpdateListener {
                            sleepTime+=1000L
                            handler.postDelayed({
                                if(wateringCan.isAnimating){
                                    val rect1 = Rect(wateringCan.left, wateringCan.top, wateringCan.right, wateringCan.bottom)
                                    val rect2 = Rect(treeImg.left, treeImg.top, treeImg.right, treeImg.bottom)

                                    //aumento il numero di passi totali ogni secondo che l'albero viene annaffiato

                                    if (rect1.intersect(rect2)) {
                                        totalSteps+=75
                                        stepsUpdated=true
                                    }

                                    if(waterLiters>0) {
                                        waterLiters--
                                        wateringCanText.text = "${waterLiters}L"
                                    }

                                    requireContext().getSharedPreferences("trackingPrefs",Context.MODE_PRIVATE)
                                        .edit()
                                        .putInt("waterLiters",waterLiters).apply()

                                    if(waterLiters==0&&wateringCan.speed>0){
                                        wateringCan.setMinFrame(0)
                                        wateringCan.speed=-wateringCan.speed

                                        Toast.makeText(requireContext(),"Acqua finita!",Toast.LENGTH_LONG).show()

                                        wateringCan.removeAllUpdateListeners()
                                    }
                                }
                            },sleepTime)
                        }
                    }
                }
                return true
            }
            MotionEvent.ACTION_UP-> {
                //resetto la posizione dell'oggetto
                wateringCan.removeAllUpdateListeners()
                //se i passi sono aumentati perché l'utente ha annaffiato l'albero,
                // li salvo nelle shared prefs e resetto l'albero (perché potrebbe essere cresciuto)
                if(stepsUpdated) {
                    requireContext().getSharedPreferences("trackingPrefs", Context.MODE_PRIVATE)
                        .edit()
                        .putLong("steps", totalSteps).commit()
                    stepsUpdated=false
                    setTree()
                }
                wateringCan.cancelAnimation()
                wateringCan.setMinFrame(0)
                wateringCan.progress=0F
                wateringCanBox.animate()
                    .translationY(0F)
                    .translationX(0F)

                binding.wateringCanText.visibility=View.INVISIBLE
                return true
            }
        }
        return true
    }

    //funzione che genera i frutti
    fun spawnFruits(){
        val fruitsContainer=binding.fruitsContainer
        fruitsContainer.visibility=View.VISIBLE

        var currentFruitX=fruitsShPrefs.getFloat("fruit0X",0F)
        //controllo se esiste già almeno un frutto nelle shared
        //in caso contrario faccio partire l'algoritmo di spawn dei frutti
        if(currentFruitX==0F){
            for(i in 0..fruitMinNumber+(Random.nextInt(fruitMaxNumber-fruitMinNumber))){
                fruitsContainer.post {
                    val dp=resources.displayMetrics.density

                    val width=(fruitWidth*dp).toInt()
                    val height=(fruitHeight*dp).toInt()
                    val x = 0.1F + Random.nextInt(fruitsContainer.width - width).toFloat()
                    val y = Random.nextInt(fruitsContainer.height - height).toFloat()
                    val imgNumber = Random.nextInt(fruitsMaxResIndex)

                    createFruitView(x, y, width, height, imgNumber)

                    fruitsShPrefs.edit()
                        .putFloat("fruit${i}X",x)
                        .putFloat("fruit${i}Y",y)
                        .putInt("fruit${i}W",width)
                        .putInt("fruit${i}H",height)
                        .putInt("fruit${i}Img",imgNumber)
                        .apply()
                }
            }
        }
        else{
            var i=0

            while(currentFruitX!=0F){
                val y=fruitsShPrefs.getFloat("fruit${i}Y",0F)
                val width=fruitsShPrefs.getInt("fruit${i}W",0)
                val height=fruitsShPrefs.getInt("fruit${i}H",0)
                val imgNumber=fruitsShPrefs.getInt("fruit${i}Img",0)

                createFruitView(currentFruitX,y,width,height,imgNumber)

                i++
                currentFruitX=fruitsShPrefs.getFloat("fruit{$i}X",0F)
            }
        }

        fruitsContainer.bringToFront()
    }
    //metodo per creare l'imageView di un frutto
    fun createFruitView(x:Float,y:Float,width:Int,height:Int,imgNumber:Int){
        val newFruit = ImageView(requireContext())
        binding.fruitsContainer.addView(newFruit)
        newFruit.x = x
        newFruit.y = y
        newFruit.layoutParams.width = width
        newFruit.layoutParams.height = height
        Log.d("fruitID",width.toString())
        Log.d("fruitID",fruitResId+imgNumber)
        val imgUri = resources.getIdentifier(fruitResId + imgNumber, null, requireActivity().packageName)
        //val imgUri=resources.getIdentifier("@drawable/tree_frame_1",null,requireActivity().packageName)
        newFruit.setImageDrawable(ResourcesCompat.getDrawable(resources, imgUri, null))

        fruitsList.add(newFruit)
    }
}