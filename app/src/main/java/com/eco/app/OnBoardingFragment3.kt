package com.eco.app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class OnBoardingFragment3 : Fragment() {

    private lateinit var btnEndIntro : FloatingActionButton
    private lateinit var txtSkip : TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_on_boarding3, container, false) as ViewGroup
        btnEndIntro = root.findViewById(R.id.btn_endIntro)
        txtSkip = root.findViewById(R.id.txt_skip3)

        btnEndIntro.setOnClickListener(View.OnClickListener {
            val intent=Intent(activity,First_Activity::class.java)
            startActivity(intent)
        })

        txtSkip.setOnClickListener(View.OnClickListener {
            val intent=Intent(activity,First_Activity::class.java)
            startActivity(intent)
        })

        return root
    }
}