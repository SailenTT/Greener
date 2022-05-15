package com.eco.app

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eco.app.databinding.ActivityLoginPageBinding
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
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginPage : AppCompatActivity() {
    private lateinit var binding: ActivityLoginPageBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var callbackManager: CallbackManager
    private val REQ_ONE_TAP = 2
    /*variabile UID utile da portare in giro, settato al momento del login
      per query
     */
    companion object{
        var UID = ""
    }

    //COMMENTI GIUSTO PER AVERE UN MINIMO DI ORDINE NEL CODICE
    //POI LI RIFACCIAMO
    //metodo onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        callbackManager = CallbackManager.Factory.create()

        //assegno l'oggetto grafico della UI alla variabile
        val txtSignUp = binding.txtSignUp

        //metodo onClick della txtSignUp per far diventare la txt un link per la activity RegisterPage
        txtSignUp.setOnClickListener {
            val intent= Intent(this, RegisterPage::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        //metodo onClick del btnLogin
        binding.btnLogin.setOnClickListener{
            loginUser()
        }

        binding.btnLoginFacebook.background=getDrawable(R.drawable.btn_rounded_green_bg)

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
                    val alertDialogBuilder = AlertDialog.Builder(this@LoginPage)
                    alertDialogBuilder.setTitle("Facebook Error")
                    alertDialogBuilder.setMessage("Errore con le api facebook")
                    alertDialogBuilder.show()
                }
            })

        //metodo onClick del btnLoginGoogle
        binding.btnLoginGoogle.setOnClickListener{
            loginWithGoogle()
        }

        auth=Firebase.auth

        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.google_server_client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .setAutoSelectEnabled(true)
            .build()
    }

    override fun onStart() {
        super.onStart()
        val user=auth.currentUser
        //TODO caricare la prossima pagina
    }



    //COMMENTI GIUSTO PER AVERE UN MINIMO DI ORDINE NEL CODICE
    //POI LI RIFACCIAMO
    //funzione per il login dell'utente
    fun loginUser(){
        val email=binding.edtEmail.text.toString()
        val pswd=binding.edtPsw.text.toString()


        if (email.equals("")) {
            binding.edtEmail.setError("Check mail")
            return
        }

        if (pswd.equals("")) {
            binding.edtPsw.setError("Check password")
            return
        }

        else {
            auth.signInWithEmailAndPassword(email, pswd)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        val uid = auth.uid
                        //TODO Load main dashboard
                        if (uid != null) {
                            UID = uid
                        }
                        val intent = Intent(this,DebugActivity::class.java)
                        startActivity(intent)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

    }

    //COMMENTI GIUSTO PER AVERE UN MINIMO DI ORDINE NEL CODICE
    //POI LI RIFACCIAMO
    //funzione per gestire il login con google(firebase)
    fun loginWithGoogle(){
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(this) { result ->
                try {
                    println("carico la UI")
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQ_ONE_TAP,
                        null, 0, 0, 0, null)
                } catch (e: IntentSender.SendIntentException) {
                    Toast.makeText(this,"Error nel caricamento del login", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(this) { e ->
                // Error loading both signin and signup
                Log.d(TAG, e.localizedMessage)
            }

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
                    val intent = Intent(this,HomeWindow::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
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
    //COMMENTI GIUSTO PER AVERE UN MINIMO DI ORDINE NEL CODICE
    //POI LI RIFACCIAMO
    //funzione che controlla il risultato dell'operazione eseguita
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_ONE_TAP -> {
                val googleCredential = oneTapClient.getSignInCredentialFromIntent(data)
                val idToken = googleCredential.googleIdToken

                when {
                    idToken != null -> {
                        // Got an ID token from Google. Use it to authenticate
                        // with Firebase.
                        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                        auth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithCredential:success")
                                    val user = auth.currentUser
                                    Toast.makeText(this, "Login Effettuato Fra", Toast.LENGTH_SHORT)
                                        .show()
                                    //TODO carica prossima UI
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                                    Toast.makeText(
                                        this,
                                        "Utente non registrato",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }
                    }
                    else -> {
                        // Shouldn't happen.
                        Log.d(TAG, "No ID token!")
                    }
                }
            }
            else -> {
                callbackManager.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

}