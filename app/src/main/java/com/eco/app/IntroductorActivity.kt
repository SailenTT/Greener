package com.eco.app

import android.R
import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.airbnb.lottie.LottieAnimationView
import com.eco.app.databinding.ActivityIntroductorBinding


class IntroductorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntroductorBinding
    private lateinit var logo: ImageView
    private lateinit var slogan: ImageView
    private lateinit var splashImg: ImageView
    private lateinit var lottie: LottieAnimationView
    //private lateinit var anim : Animation

    private lateinit var viewPager: ViewPager
    private var pagerAdapter: ScreenSlidePagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityIntroductorBinding.inflate(layoutInflater)

        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        //binding con gli oggetti grafici della schermata Activity_Introductor.xml
        slogan = binding.slogan
        splashImg = binding.img
        logo = binding.logo
        lottie = binding.lottie

        //anim = AnimationUtils.loadAnimation(this, R.anim.o_b_anim)

        viewPager = binding.pager
        pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        viewPager.setAdapter(pagerAdapter)

        //animazione Activity_Introductor.xml
        //velocità originale 1400
        //FATTO
        splashImg.animate().translationY(-3000f).setDuration(1400).startDelay = 3500
        slogan.animate().translationY(1800f).setDuration(1400).startDelay = 3500
        logo.animate().translationY(1800f).setDuration(1400).setStartDelay(3500)
        lottie.animate().translationY(1600f).setDuration(1400).setStartDelay(3500)

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

    class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        private val NUM_PAGES = 3

        override fun getItem(position: Int): Fragment {
            when (position) {
                0 -> {
                    return OnBoardingFragment1()
                }
                1 -> {
                    return OnBoardingFragment2()
                }
                2 -> {
                    return OnBoardingFragment3()
                }
            }
            return Fragment()
        }

        override fun getCount(): Int {
            return NUM_PAGES
        }
    }
}