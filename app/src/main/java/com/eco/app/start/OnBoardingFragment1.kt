package com.eco.app.start

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.eco.app.R

class OnBoardingFragment1 : Fragment() {

    private lateinit var txtSkip: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_on_boarding1, container, false) as ViewGroup
        txtSkip = root.findViewById(R.id.txt_skip1)
        txtSkip.setOnClickListener(View.OnClickListener {
            val sharedPreferences =
                activity?.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences?.edit()
            editor?.apply {
                putInt("skip", 1);
            }?.apply()
            val intent = Intent(activity, First_Activity::class.java)
            startActivity(intent)
        })

        return root
    }

    private fun checkSkip(): Int {
        val sharedPreferences = activity?.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val skipped = sharedPreferences!!.getInt("skip", 0)
        return skipped
    }
}