package com.eco.app

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.eco.app.databinding.ActivityRegisterPageBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegisterPage : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var binding: ActivityRegisterPageBinding
    private lateinit var callbackManager: CallbackManager
    private val REQ_ONE_TAP = 2
    val TAG = "FACEBOOKTAG"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        callbackManager = CallbackManager.Factory.create()

        auth = FirebaseAuth.getInstance()
        database =
            Firebase.database("https://ecoapp-706b8-default-rtdb.europe-west1.firebasedatabase.app/")

        binding.btnRegister.setOnClickListener {
            createAccount()
        }

        //FACEBOOK REGISTER
        binding.btnLoginFacebook.registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Log.d(TAG, "facebook:onSuccess$loginResult")
                    handleFacebookAccessToken(loginResult.accessToken)
                }
                override fun onCancel() {
                    Log.d(TAG, "facebook:onCancel")
                }

                override fun onError(error: FacebookException) {
                    Log.d(TAG, "facebook:onError", error)
                    val alertDialogBuilder = AlertDialog.Builder(this@RegisterPage)
                    alertDialogBuilder.setTitle("Facebook Error")
                    alertDialogBuilder.setMessage("Errore con le api facebook")
                    alertDialogBuilder.show()
                }
            })

        //TODO GOOGLE REGISTER



    }



    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Log.d(TAG, "signInWithCredential:success")
                    val userid = auth.currentUser
                    //TODO redirectare l'utente alla pagina main e qui mettere finish() inoltre togliere dal backstack
                } else {
                    // Sign in fail
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
        //TODO dobbiamo avere piu onactivity, ma le api usano tutti metodi deprecati
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
            Toast.makeText(this, "$requestCode", Toast.LENGTH_SHORT).show()
        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)
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