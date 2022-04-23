package com.eco.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.airbnb.lottie.LottieAnimationView

class IntroductorActivity : AppCompatActivity() {

    private lateinit var splashImg : ImageView
    private lateinit var  logo : ImageView
    private lateinit var lottie : LottieAnimationView
    private lateinit var slogan : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introductor)


        slogan = findViewById(R.id.slogan)
        splashImg = findViewById(R.id.img)
        logo = findViewById(R.id.logo)
        lottie = findViewById(R.id.lottie)

        splashImg.animate().translationY(-3000F).setDuration(1400).setStartDelay(3500)
        slogan.animate().translationY(1800F).setDuration(1400).setStartDelay(3500)
        logo.animate().translationY(1800F).setDuration(1400).setStartDelay(3500)
        lottie.animate().translationY(1600F).setDuration(1400).setStartDelay(3500)
    }
}