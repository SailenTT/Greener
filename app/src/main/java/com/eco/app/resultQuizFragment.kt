package com.eco.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eco.app.databinding.FragmentResultQuizBinding


/**
 * A simple [Fragment] subclass.
 * Use the [resultQuizFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class resultQuizFragment : Fragment() {
    private lateinit var binding : FragmentResultQuizBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResultQuizBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        binding.tvQuizResult.setText("Risultato: ${QuizFragment.correct_replies}/10")
        return binding.root
    }


}