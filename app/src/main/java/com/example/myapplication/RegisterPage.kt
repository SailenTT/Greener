package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myapplication.databinding.ActivityRegisterPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegisterPage : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var binding : ActivityRegisterPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            createAccount()
        }
        auth = FirebaseAuth.getInstance()

        database=Firebase.database.reference
    }



    fun createAccount() {
        val name= binding.txtNome.text.toString()
        val email = binding.txtEmail.text.toString()
        val psw = binding.txtPsw.text.toString()
        if (binding.txtEmail.text.toString().equals("") || binding.txtPsw.text.toString().equals("")
            ||name.equals("")) {
            Toast.makeText(this, "Controlla i dati inseriti", Toast.LENGTH_SHORT).show()
        } else {
            auth.createUserWithEmailAndPassword(email, psw).addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Utente registrato correttamente", Toast.LENGTH_SHORT)
                        .show()
                    val userUid=task.result.user!!.uid

                    database.child("users").child(userUid).child("username").setValue(name)
                } else {
                    Toast.makeText(this, "Errore nella registrazione", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}