package com.eco.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eco.app.databinding.FragmentGameSelectionBinding

/**
 * A simple [Fragment] subclass.
 */
class GameSelectionFragment : Fragment() {
    private lateinit var binding: FragmentGameSelectionBinding
    private lateinit var fragmentContainer: ViewGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentGameSelectionBinding.inflate(inflater,container,false)

        binding.trashBinGameName.setOnClickListener {
            startTrashBinGame()
        }
        binding.trashBingGameImg.setOnClickListener{
            startTrashBinGame()
        }

        binding.garbageSorterGameName.setOnClickListener {
            startGarbageSorterGame()
        }
        binding.garbageSorterGameImg.setOnClickListener {
            startGarbageSorterGame()
        }

        binding.quizGameName.setOnClickListener {
            startQuizGame()
        }
        binding.quizGameImg.setOnClickListener {
            startQuizGame()
        }

        binding.growingTreeGameName.setOnClickListener {
            startGrowingTreeGame()
        }
        binding.garbageSorterGameImg.setOnClickListener {
            startGrowingTreeGame()
        }

        return binding.root
    }

    fun startTrashBinGame(){
        val transaction=parentFragmentManager.beginTransaction()
        transaction.replace(R.id.home_fragment_container,TrashBinGame())
        transaction.commit()
    }

    fun startGarbageSorterGame(){
        val transaction=parentFragmentManager.beginTransaction()
        transaction.replace(R.id.home_fragment_container,GarbageSorterGame())
        transaction.commit()
    }

    fun startQuizGame(){
        val transaction=parentFragmentManager.beginTransaction()
        transaction.replace(R.id.home_fragment_container,QuizFragment())
        transaction.commit()
    }

    fun startGrowingTreeGame(){

    }
}