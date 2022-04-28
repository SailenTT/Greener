package com.eco.app

import android.graphics.Color
import kotlin.coroutines.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.eco.app.databinding.ActivityQuizBinding
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

class QuizActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizBinding
    val quizFragment = QuizFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFragment()

    }

     fun setFragment(){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(binding.fragmentContainerQuiz.id,quizFragment)
    }


    override fun onStop() {
        super.onStop()
        finish()
    }







}

