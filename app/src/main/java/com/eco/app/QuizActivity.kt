package com.eco.app

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
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
    private lateinit var hashMap : HashMap<Int,String>
    private lateinit var reply : String
    private lateinit var correctreply : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database =
            Firebase.database(RegisterPage.PATHTODB)

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
            val correctreply_db = question_db.child("risp0").get().addOnSuccessListener {
                correctreply = it.value.toString()
            }
            val replies = question_db.child("risp$i").get().addOnSuccessListener {
                buttonsArray[randomList_buttons.get(i)]?.setText(it.value.toString())
                buttonsArray[i]?.setOnClickListener {
                    reply = buttonsArray[i]?.text.toString()
                    checkreply(reply, correctreply, i)

                }
            }
        }
    }

    fun checkreply(reply : String,correctReply : String, position : Int){
        //TODO CHECKREPLY()
        if (reply==correctReply){
            Toast.makeText(this, "RISPOSTA ESATTA", Toast.LENGTH_SHORT).show()
            setQuiz(txt_question,buttons)
        }else{
            Toast.makeText(this, "RISPOSTA ERRATA", Toast.LENGTH_SHORT).show()
            setQuiz(txt_question,buttons)
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
        for(i in 0..3){
            array[i]?.setBackgroundColor(ContextCompat.getColor(this,R.color.greenLogin))
        }
        return array
    }

}

