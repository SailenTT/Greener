package com.eco.app

import android.R
import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.eco.app.databinding.ActivityIntroductorBinding


class IntroductorActivity : AppCompatActivity() {

    private lateinit var binding:ActivityIntroductorBinding
    private lateinit var splashImg : ImageView
    private lateinit var  logo : ImageView
    private lateinit var lottie : LottieAnimationView
    private lateinit var slogan : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityIntroductorBinding.inflate(layoutInflater)

        setContentView(binding.root)


        slogan = binding.slogan
        splashImg = binding.img
        logo = binding.logo
        lottie = binding.lottie

        //velocità originale 1400
        //TODO mettere con velocità massima a 1000ms
        splashImg.animate().translationY(-3000F).setDuration(1200).setStartDelay(3500)
        slogan.animate().translationY(1800F).setDuration(1200).setStartDelay(3500)
        logo.animate().translationY(1800F).setDuration(1200).setStartDelay(3500)
        lottie.animate().translationY(1600F).setDuration(1200).setStartDelay(3500)

        val lottieView = binding.lottie
        lottieView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {}

            override fun onAnimationEnd(animator: Animator) {
                //TODO cambiare e far partire l'homepage (una volta pronta)
                val intent=Intent(this@IntroductorActivity,debug_activity::class.java)
                startActivity(intent)
            }

            override fun onAnimationCancel(animator: Animator) {}
            override fun onAnimationRepeat(animator: Animator) {}
        })

    }
}