package com.eco.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.eco.app.databinding.ActivityFirstBinding
import com.eco.app.databinding.ActivityIntroductorBinding

class First_Activity : AppCompatActivity() {

    private lateinit var binding: ActivityFirstBinding
    private lateinit var btnLog : Button
    private lateinit var btnSign : Button
    private lateinit var btnDeb : Button
    private lateinit var txtWho : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)

        binding= ActivityFirstBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnLog = binding.btnFirstLogin
        btnSign = binding.btnFirstSignup
        btnDeb = binding.btnDebugFirst
        txtWho = binding.txtWho

        btnLog.setOnClickListener(View.OnClickListener {
            val intent= Intent(this,LoginPage::class.java)
            startActivity(intent)
        })

        btnSign.setOnClickListener(View.OnClickListener {
            val intent=Intent(this,RegisterPage::class.java)
            startActivity(intent)
        })

        btnDeb.setOnClickListener(View.OnClickListener {
            val intent=Intent(this,debug_activity::class.java)
            startActivity(intent)
        })
    }
}