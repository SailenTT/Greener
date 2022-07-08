package com.eco.app.profile

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.eco.app.HomeWindow
import com.eco.app.R
import com.eco.app.databinding.FragmentLoginBinding
import com.eco.app.start.DebugActivity
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import org.json.JSONException


class LoginFragment : Fragment() {
    //TODO EVITARE DOPPI LOGIN GOOGLE FACEBOOK FIREBASE
    private lateinit var binding: FragmentLoginBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var callbackManager: CallbackManager
    private lateinit var progressBar: ProgressBar
    private lateinit var loginPageContainer: RelativeLayout
    private val REQ_ONE_TAP = 2
    private var showOneTapUI = true

    /*variabile UID utile da portare in giro, settato al momento del login
      per query
     */
    companion object {
        var UID = ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        loginPageContainer = binding.loginPageContainer
        progressBar = binding.progressBar
        database = Firebase.database(RegisterPage.PATHTODB)
        callbackManager = CallbackManager.Factory.create()
        auth = Firebase.auth

        //assegno l'oggetto grafico della UI alla variabile
        val txtSignUp = binding.txtSignUp

        //metodo onClick della txtSignUp per far diventare la txt un link per la activity RegisterPage
        txtSignUp.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionFromLoginToRegister())
        }

        //metodo onClick del btnLogin
        binding.btnLogin.setOnClickListener {
            if(auth.currentUser == null){
                loginUser()
            }else{
                Toast.makeText(context, "Sei già loggato", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLoginFacebook.background =
            AppCompatResources.getDrawable(requireContext(), R.drawable.btn_rounded_green_bg)

        binding.btnLoginFacebook.registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    Log.d(ContentValues.TAG, "facebook:onSuccess$result")
                    handleFacebookAccessToken(result.accessToken)
                }

                override fun onCancel() {
                    Log.d(ContentValues.TAG, "facebook:onCancel")
                }

                override fun onError(error: FacebookException) {
                    Log.d(ContentValues.TAG, "facebook:onError", error)
                    val alertDialogBuilder = AlertDialog.Builder(requireContext())
                    alertDialogBuilder.setTitle("Facebook Error")
                    alertDialogBuilder.setMessage("Errore con le api facebook")
                    alertDialogBuilder.show()
                }
            })

        //metodo onClick del btnLoginGoogle
        binding.btnLoginGoogle.setOnClickListener {
            loginWithGoogle()
        }


        //activity.this in alternativa
        oneTapClient = Identity.getSignInClient(requireContext())
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.google_server_client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        if (auth.currentUser != null) {
            goBackToHomepage()
        }
    }


    //COMMENTI GIUSTO PER AVERE UN MINIMO DI ORDINE NEL CODICE
    //POI LI RIFACCIAMO
    //funzione per il login dell'utente
    fun loginUser() {
        if (auth.currentUser == null) {
            val email = binding.edtEmail.text.toString()
            val pswd = binding.edtPsw.text.toString()


            if (email.equals("")) {
                binding.edtEmail.setError("Check mail")
                return
            }

            if (pswd.equals("")) {
                binding.edtPsw.setError("Check password")
                return
            } else {
                auth.signInWithEmailAndPassword(email, pswd)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(ContentValues.TAG, "signInWithEmail:success")
                            val user = auth.currentUser
                            val uid = auth.uid
                            //TODO Load main dashboard
                            if (uid != null) {
                                UID = uid
                            }
                            val intent = Intent(context, HomeWindow::class.java)
                            startActivity(intent)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(
                                requireContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }else{
            Toast.makeText(context, "Sei già loggato", Toast.LENGTH_SHORT).show()
        }

    }

    //COMMENTI GIUSTO PER AVERE UN MINIMO DI ORDINE NEL CODICE
    //POI LI RIFACCIAMO
    //funzione per gestire il login con google(firebase)
    fun loginWithGoogle() {
        //creo una progressBar per mostrare il caricamento
        if (auth.currentUser == null) {

            val progressBar = binding.progressBar
            progressBar.visibility = View.VISIBLE

            binding.loginPageContainer.alpha = 0.7f

            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(requireActivity()) { result ->
                    try {
                        startIntentSenderForResult(
                            result.pendingIntent.intentSender, REQ_ONE_TAP,
                            null, 0, 0, 0, null
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        Toast.makeText(
                            requireContext(),
                            "Error nel caricamento del login",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e(ContentValues.TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                    }
                }
                .addOnFailureListener(requireActivity()) { e ->
                    // Error loading both signin and signup
                    Log.d(ContentValues.TAG, e.localizedMessage)
                }
        } else {
            Toast.makeText(context, "Sei già loggato", Toast.LENGTH_SHORT).show()
        }

    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(ContentValues.TAG, "handleFacebookAccessToken:$token")
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    //Toast.makeText(context, "OK", Toast.LENGTH_SHORT).show()
                    // Sign in success
                    Log.d(ContentValues.TAG, "signInWithCredential:success")
                    if(task.getResult().additionalUserInfo?.isNewUser == true){
                        val bundle = Bundle()
                        bundle.putString("fields", "id, email, first_name, last_name, gender,age_range")
                        val request = GraphRequest.newMeRequest(token) { fbObject, response ->
                            try {
                                val userid = auth.uid!!
                                val firstName = fbObject?.getString("first_name")
                                val email =fbObject?.getString("email")

                                val usersReference = database.getReference("Users")
                                usersReference.child(userid).child("username")
                                    .setValue(firstName.toString())
                                usersReference.child(userid).child("quiz_score").setValue(0)
                                usersReference.child(userid).child("bin_score").setValue(0)
                                usersReference.child(userid).child("carbon_footprint").setValue(0)
                                usersReference.child(userid).child("divide_score").setValue(0)
                                usersReference.child(userid).child("growing_tree").setValue(0)
                                usersReference.child(userid).child("email").setValue(email)
                                uploadToStorageDefaultProfilePic(userid)
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                        request.parameters = bundle
                        request.executeAsync()
                    }
                    val intent = Intent(requireContext(), HomeWindow::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                } else {
                    // Sign in fail
                    Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        context, "Authentication failed.",
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

                progressBar.visibility = View.GONE
                loginPageContainer.alpha = 1f

                when {
                    idToken != null -> {
                        // Got an ID token from Google. Use it to authenticate
                        // with Firebase.
                        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                        auth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener(requireActivity()) { task ->
                                if (task.isSuccessful) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(ContentValues.TAG, "signInWithCredential:success")
                                    val user = auth.currentUser
                                    //Toast.makeText(requireContext(), "Login Effettuato", Toast.LENGTH_SHORT).show()
                                    goBackToHomepage()
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(
                                        ContentValues.TAG,
                                        "signInWithCredential:failure",
                                        task.exception
                                    )
                                    Toast.makeText(
                                        requireContext(),
                                        "Utente non registrato",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }
                    }
                    else -> {
                        // Shouldn't happen.
                        Log.d(ContentValues.TAG, "No ID token!")
                    }
                }
            }
            else -> {
                callbackManager.onActivityResult(requestCode, resultCode, data)
            }
        }
    }


    fun goBackToHomepage() {
        //ricreo il menù delle opzioni
        requireActivity().invalidateOptionsMenu()
        findNavController().popBackStack()
        //findNavController().navigate(LoginFragmentDirections.actionFromLoginBackToHome())
    }
    fun uploadToStorageDefaultProfilePic(UID : String){
        val uri = Uri.parse("android.resource://" + requireContext().packageName.toString() + "/drawable/default_propic")
        val filename = UID
        val storageReference = FirebaseStorage.getInstance("gs://ecoapp-706b8.appspot.com").getReference("propics/$filename")
        storageReference.putFile(uri).addOnSuccessListener {
           // Toast.makeText(context, "Foto uppata", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(context, "Errore con la foto", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }
}