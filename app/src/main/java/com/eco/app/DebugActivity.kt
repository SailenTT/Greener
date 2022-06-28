package com.eco.app

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.eco.app.databinding.ActivityDebugBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class DebugActivity : AppCompatActivity() {
    private lateinit var analytics: FirebaseAnalytics
    private lateinit var auth: FirebaseAuth

    private lateinit var binding : ActivityDebugBinding
    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)
        //notifiche

        // Obtain the FirebaseAnalytics instance.
        analytics = Firebase.analytics
        //Obtain the Firebase auth
        auth = Firebase.auth;

        binding = ActivityDebugBinding.inflate(layoutInflater)
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

        binding.btnHomepage.setOnClickListener {
            val intent = Intent(this,HomeWindow::class.java)
            startActivity(intent)
        }

    }
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser!= null){
            Toast.makeText(this, "Logged in ", Toast.LENGTH_SHORT).show()
           // val intent=Intent(this,HomeWindow::class.java)
           // startActivity(intent)
        }
        else{
            Toast.makeText(this, "Not logged in ", Toast.LENGTH_SHORT).show()
            //val intent=Intent(this,LoginPage::class.java)
            //startActivity(intent)
        }
    }
}