package com.eco.app

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.eco.app.databinding.FragmentQuizBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.properties.Delegates
import kotlin.random.Random

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [QuizFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class QuizFragment : Fragment() {
    private lateinit var binding: FragmentQuizBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var buttons: Array<Button?>
    private lateinit var txt_question: TextView
    private lateinit var reply: String
    private lateinit var correctreply: String
    private var quiz_questions_number: Int = 10
    private var questions_index_list : MutableList<Int> = mutableListOf<Int>()
    val resultFragment = resultQuizFragment()
    val randomList_buttons = (0..3).shuffled().take(4)
    private var random_question by Delegates.notNull<Int>()

    companion object{
        var correct_replies = 0
        val DB_QUESTIONS = 10
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentQuizBinding.inflate(inflater, container, false)

        database =
            Firebase.database(RegisterPage.PATHTODB)

        //getto i button in un array per comodità
        buttons = getButtons()
        txt_question = binding.tvQuestion

        activity?.runOnUiThread{run{
            setQuiz(txt_question, buttons)
        }}

        return binding.root

    }

    fun setQuiz(txt_question: TextView, buttonsArray: Array<Button?>) {
        random_question = getQuestion()

        val reference = database.getReference("Quiz")
        val question_db = reference.child("${random_question + 1}")//questo shuffle a caso
        val question = question_db.child("domanda").get().addOnSuccessListener {
            txt_question.setText(it.value.toString())
            Log.i("firebase", "Question presa")
        }
        activity?.runOnUiThread{run{
            for (i in 0..3) {
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
        }}

    }

    fun getQuestion(): Int {
        var id = Random.nextInt(DB_QUESTIONS)
        Log.d("estrazione","estratto $id")
        questions_index_list.add(id)
        Log.d("estrazione",questions_index_list.toString())
        if (questions_index_list.contains(id)){
            Log.d("estrazione","Riestratto $id")
            id = Random.nextInt(DB_QUESTIONS)
        }
        return id
    }

    fun checkreply(reply: String, correctReply: String, position: Int) {
        if (reply == correctReply) {//TODO controllo , dopo 10 domande compare fragment con punteggio
            //Toast.makeText(requireActivity(), "RISPOSTA ESATTA", Toast.LENGTH_SHORT).show()
            correct_replies++
            quiz_questions_number--
            if(quiz_questions_number == 0){
                replaceFragment(resultFragment)
            }
            setQuiz(txt_question, buttons)
        } else {
            //Toast.makeText(requireActivity(), "RISPOSTA ERRATA", Toast.LENGTH_SHORT).show()
            quiz_questions_number--
            if(quiz_questions_number == 0){
                replaceFragment(resultFragment)
            }
            setQuiz(txt_question, buttons)
        }
    }

    fun replaceFragment(fragment : Fragment){
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.fragment_container_quiz,fragment)
        transaction?.commit()
    }

    fun getButtons(): Array<Button?> {
        val array: Array<Button?> = Array(4) { null }
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