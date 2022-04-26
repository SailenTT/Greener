package com.eco.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.eco.app.databinding.ActivityQuizBinding
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

class QuizActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var binding: ActivityQuizBinding
    private lateinit var buttons: Array<Button?>
    private lateinit var txt_question : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database =
            Firebase.database("https://ecoapp-706b8-default-rtdb.europe-west1.firebasedatabase.app/")

        //getto i button in un array per comodità
        buttons = getButtons()
        txt_question = binding.tvQuestion
        setQuiz(txt_question,buttons)

    }


    override fun onStop() {
        super.onStop()
        finish()
    }

    fun setQuiz(txt_question : TextView, buttonsArray : Array<Button?>){
        val randomList_buttons = (0..3).shuffled().take(4)
        val random_question = Random.nextInt(2)
        Log.i("random_question","$random_question")
        val reference = database.getReference("Quiz")
        val question_db = reference.child("${random_question+1}")//questo shuffle a caso
        val question = question_db.child("domanda").get().addOnSuccessListener {
            txt_question.setText(it.value.toString())
            Log.i("firebase","Question presa")
        }
        for(i in 0..3){
            val replies = question_db.child("risp$i").get().addOnSuccessListener {
                buttonsArray[randomList_buttons.get(i)]?.setText(it.value.toString())
            }
        }
    }

    fun getButtons(): Array<Button?>{
        val array : Array<Button?> = Array(4){null}
        val btnR1 = binding.btnR1
        val btnR2 = binding.btnR2
        val btnr3 = binding.btnR3
        val btnr4 = binding.btnR4
        array[0] = btnR1
        array[1] = btnR2
        array[2] = btnr3
        array[3] = btnr4
        return array
    }

}

