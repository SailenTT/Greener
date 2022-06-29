package com.eco.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.eco.app.databinding.FragmentGameSelectionBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialElevationScale
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * A simple [Fragment] subclass.
 */
class GameSelectionFragment : Fragment() {
    private lateinit var binding: FragmentGameSelectionBinding
    private lateinit var currentUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        exitTransition = null
        reenterTransition = null

        binding= FragmentGameSelectionBinding.inflate(inflater,container,false)


        binding.trashBinGameCard.setOnClickListener{view->
            val extras = setTransitionAnim(view, getString(R.string.trash_bin_game_transition))
            findNavController().navigate(GameSelectionFragmentDirections.actionGameSelectionFragmentToTrashBinGame(), extras)
        }

        binding.garbageSorterGameCard.setOnClickListener {view->
            val extras= setTransitionAnim(view,getString(R.string.garbage_sorter_game_transition))
            findNavController().navigate(GameSelectionFragmentDirections.actionGameSelectionFragmentToGarbageSorterGame(),extras)
        }

        binding.quizGameCard.setOnClickListener {view->
            val extras= setTransitionAnim(view,getString(R.string.quiz_game_transition))
            findNavController().navigate(GameSelectionFragmentDirections.actionGameSelectionFragmentToQuizFragment(),extras)
        }

        binding.growingTreeGameCard.setOnClickListener {view->
            val extras= setTransitionAnim(view,getString(R.string.growing_tree_game_transition))
            findNavController().navigate(GameSelectionFragmentDirections.actionGameSelectionFragmentToGrowingTreeFragment(),extras)
        }

        return binding.root
    }

    fun setTransitionAnim(view :View,animName: String):FragmentNavigator.Extras{
        exitTransition = MaterialElevationScale(false)
        reenterTransition = MaterialElevationScale(true)

        return FragmentNavigatorExtras(
            view to animName
        )
    }

}