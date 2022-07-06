package com.eco.app.games

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.eco.app.databinding.FragmentResultQuizBinding


/**
 * A simple [Fragment] subclass.
 * Use the [ResultQuizFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ResultQuizFragment : Fragment() {
    private lateinit var binding : FragmentResultQuizBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResultQuizBinding.inflate(inflater, container, false)



        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,  object: OnBackPressedCallback(true) {
            @Override
            override fun handleOnBackPressed() {
                findNavController().navigate(ResultQuizFragmentDirections.actionQuizResultFragmentToGameSelectionFragment())
            }
        })
        // Inflate the layout for this fragment
        binding.tvQuizResult.text = "Risultato: ${QuizFragment.correct_replies}/10"
        return binding.root
    }


}