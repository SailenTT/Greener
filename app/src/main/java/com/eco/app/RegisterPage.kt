package com.eco.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eco.app.databinding.ActivityRegisterPageBinding
import com.facebook.CallbackManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File


//TODO eliminare questa activity (perché è stata sostituita dal suo fragment)
class RegisterPage : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var binding: ActivityRegisterPageBinding
    private lateinit var callbackManager: CallbackManager

    companion object{
        val PATHTODB="https://ecoapp-706b8-default-rtdb.europe-west1.firebasedatabase.app/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //assegno l'oggetto grafico della UI alla variabile
        val txtLogIn = binding.txtLogIn

        //metodo onClick della txtSignUp per far diventare la txt un link per la activity RegisterPage
        txtLogIn.setOnClickListener {
            val intent = Intent(this, LoginPage::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        auth = FirebaseAuth.getInstance()
        database = Firebase.database(PATHTODB)

        binding.btnRegister.setOnClickListener {
            createAccount()
        }

    }

    private fun createAccount() {
        if(auth.currentUser==null){
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
                    //Toast.makeText(this, "Utente registrato correttamente", Toast.LENGTH_SHORT)
                     //   .show()
                    val userUid = task.result.user!!.uid
                    val usersReference = database.getReference("Users")
                    usersReference.child(userUid).child("username").setValue(name)
                    usersReference.child(userUid).child("quiz_score").setValue(0)
                    usersReference.child(userUid).child("bin_score").setValue(0)
                   // uploadStandardPropic(userUid)
                } else {
                    Toast.makeText(this, "Errore nella registrazione", Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            Toast.makeText(this, "Sei gia loggato", Toast.LENGTH_SHORT).show()
            Log.d("Register","GIA LOGGATO")
        }
    }
   /* private fun uploadStandardPropic(UID: String){
        val filename = UID
        val storageReference = FirebaseStorage.getInstance("gs://ecoapp-706b8.appspot.com").getReference("propics/$filename")
        val file =Uri.parse("res/drawable/logo_app.png")
        storageReference.putFile(file).addOnSuccessListener {
            Toast.makeText(this, "uppata immagine", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(this, "immagine non uppata", Toast.LENGTH_SHORT).show()
        }
    }

    */
}