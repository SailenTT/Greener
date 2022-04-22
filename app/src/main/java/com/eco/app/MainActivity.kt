package com.eco.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.eco.app.databinding.ActivityMainBinding
import com.facebook.AccessToken
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var analytics: FirebaseAnalytics
    private lateinit var auth: FirebaseAuth

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            //startActivity(intent)
            startActivityForResult(intent,1)

        }

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
        }

    }

    //check if user is logged in
    //funzione per vedere se l'utente è già loggato
    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser!= null){
            Toast.makeText(this, "Logged in ", Toast.LENGTH_SHORT).show()
        }
    }
}