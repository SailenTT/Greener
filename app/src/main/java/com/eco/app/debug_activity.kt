package com.eco.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eco.app.databinding.ActivityMainBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class debug_activity : AppCompatActivity() {
    private lateinit var analytics: FirebaseAnalytics
    private lateinit var auth: FirebaseAuth

    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)

        // Obtain the FirebaseAnalytics instance.
        analytics = Firebase.analytics
        //Obtain the Firebase auth
        auth = Firebase.auth;

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerMain.setOnClickListener {
            val intent = Intent(this, RegisterPage::class.java)
            startActivity(intent)
        }

        binding.loginMain.setOnClickListener {
            val intent = Intent(this,LoginPage::class.java)
            startActivity(intent)

        }

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

        }

        binding.btnCalendar.setOnClickListener {
            val intent = Intent(this,HomeWindow::class.java)
            startActivity(intent)
        }
    }
}