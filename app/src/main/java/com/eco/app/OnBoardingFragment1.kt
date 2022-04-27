package com.eco.app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class OnBoardingFragment1 : Fragment() {

    private lateinit var txtSkip : TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_on_boarding1, container, false) as ViewGroup
        txtSkip = root.findViewById(R.id.txt_skip1)

        txtSkip.setOnClickListener(View.OnClickListener {
            val intent= Intent(activity,debug_activity::class.java)
            startActivity(intent)
        })

        return root
    }
}