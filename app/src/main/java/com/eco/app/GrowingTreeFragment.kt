package com.eco.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.eco.app.databinding.FragmentGrowingTreeBinding

class GrowingTreeFragment : Fragment() {
    private lateinit var binding: FragmentGrowingTreeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentGrowingTreeBinding.inflate(inflater,container,false)

        return binding.root
    }
}