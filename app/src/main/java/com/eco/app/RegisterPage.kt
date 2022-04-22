package com.eco.app

import android.R.attr
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eco.app.databinding.ActivityRegisterPageBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class RegisterPage : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var binding: ActivityRegisterPageBinding
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //assegno l'oggetto grafico della UI alla variabile
        val txtLogIn = binding.txtLogIn

        //metodo onClick della txtSignUp per far diventare la txt un link per la activity RegisterPage
        txtLogIn.setOnClickListener {
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
        }


        auth = FirebaseAuth.getInstance()
        database =
            Firebase.database("https://ecoapp-706b8-default-rtdb.europe-west1.firebasedatabase.app/")

        binding.btnRegister.setOnClickListener {
            createAccount()
        }

    }

    fun createAccount() {
        val name = binding.edtNome.text.toString()
        val email = binding.edtEmail.text.toString()
        val psw = binding.edtPsw.text.toString()

        if (name.equals("")) {
            binding.edtNome.setError("Check name")
            return
        }
        if (email.equals("")) {
            binding.edtEmail.setError("Check mail")
            return
        }

        if (psw.equals("")) {
            binding.edtPsw.setError("Check password")
            return
        }

        auth.createUserWithEmailAndPassword(email, psw).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Utente registrato correttamente", Toast.LENGTH_SHORT)
                    .show()
                val userUid = task.result.user!!.uid
                val reference = database.getReference("Users")
                reference.child(userUid).child("username").setValue(name)
            } else {
                Toast.makeText(this, "Errore nella registrazione", Toast.LENGTH_SHORT).show()
            }
        }
    }
}