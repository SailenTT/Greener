package com.eco.app.profile

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import com.eco.app.HomeWindow
import com.eco.app.R
import com.eco.app.databinding.FragmentProfileBinding
import com.facebook.AccessToken
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File


class ProfileFragment : Fragment() {
    private var facebookAccessToken: AccessToken? = null
    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var imguri : Uri
    private lateinit var UID : String
    private var loadedProfileLayout: ScrollView?=null
    private lateinit var propic: ImageView
    private lateinit var tv_username: TextView
    private lateinit var deleteAccountButton : Button
    private var firstInfoLoaded=false
    private var quizGameScore=0L
    private var trashBinGameScore=0L
    private var garbageSorterGameScore=0L
    private var carbonFootprintScore=0L
    private var username: CharSequence=""
    private var email: CharSequence=""
    private var growingTreeScore =0L
    private var leaderboardPosition=0
    private lateinit var profileImg:Bitmap
    private lateinit var usersReference : DatabaseReference
    private var loadDefault = false

    //Upload foto
    //CONTRATTI
    val register = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            //Toast.makeText(requireContext(), "APPOSTO REGISTER LAUNCHER", Toast.LENGTH_SHORT).show()
            imguri = it.data?.data!!
            val profilePicBitmap = requireContext().let { it1 -> decodeUri(it1,imguri,230) }
            propic.setImageBitmap(profilePicBitmap)
            uploadToStorage(UID)

        }
    }
    //CONTRATTI

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentProfileBinding.inflate(inflater,container,false)
        facebookAccessToken = AccessToken?.getCurrentAccessToken()
        binding.profileShimmer.startShimmer()
        auth = Firebase.auth
        val user = auth.currentUser;
        database = Firebase.database(getString(R.string.path_to_db))
        usersReference = database.getReference("Users")
        if(user==null){
            Log.i("LoginInfo","Non sei loggato")
        }else{
            UID = user.uid
            getInfos(usersReference,UID)
        }

        return binding.root
    }

    fun uploadToStorage(UID : String){
        val filename = UID
        val storageReference = FirebaseStorage.getInstance("gs://ecoapp-706b8.appspot.com").getReference("propics/$filename")
        storageReference.putFile(imguri).addOnSuccessListener {
            Toast.makeText(context, "Foto uppata", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(context, "Non uppata la foto", Toast.LENGTH_SHORT).show()
        }
    }

    fun decodeUri(c: Context, uri: Uri?, requiredSize: Int): Bitmap? { //FUNZIONE PER RESIZARE L'IMMAGINE
        val o = BitmapFactory.Options()
        o.inJustDecodeBounds = true
        BitmapFactory.decodeStream(uri?.let { c.getContentResolver().openInputStream(it) }, null, o)
        var width_tmp = o.outWidth
        var height_tmp = o.outHeight
        var scale = 1
        while (true) {
            if (width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize) break
            width_tmp /= 2
            height_tmp /= 2
            scale *= 2
        }
        val o2 = BitmapFactory.Options()
        o2.inSampleSize = scale
        return BitmapFactory.decodeStream(uri?.let { c.getContentResolver().openInputStream(it) }, null, o2)
    }

    private fun getInfos(usersReference: DatabaseReference, UID: String) { //todo se non ho img settare quella default
        if (auth.currentUser != null) {
            val filename = UID
            val storageReference = FirebaseStorage.getInstance("gs://ecoapp-706b8.appspot.com")
                .getReference("propics/$filename")
            val localfile = File.createTempFile("tempImage", "jpg")
            storageReference.getFile(localfile).addOnSuccessListener {
                //val resized = decodeUri(requireContext(),Uri.fromFile(localfile),230)
                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                profileImg = Bitmap.createScaledBitmap(bitmap, 400, 400, true)
                loadDefault=false
                Log.d("QUERY_GETINFO","IMG PRESA")
                if(firstInfoLoaded){
                    setUserData()
                }
                else{
                    firstInfoLoaded=true
                }

            }.addOnFailureListener {
                firstInfoLoaded=true
                loadDefault=true
                setUserData()

            }

            usersReference.child(UID).get().addOnSuccessListener {
                username = it.child("username").value as CharSequence
                email = it.child("email").value as CharSequence
                trashBinGameScore = it.child("bin_score").value as Long
                quizGameScore = it.child("quiz_score").value as Long
                carbonFootprintScore = it.child("carbon_footprint").value as Long
                garbageSorterGameScore = it.child("divide_score").value as Long
                growingTreeScore = it.child("growing_tree").value as Long

                if (firstInfoLoaded) {
                    setUserData()
                    Log.d("QUERY_GETINFO","PRESO IL RESTO DEI DATI")
                    /*binding.tvName.text = username
                    binding.tvQuizscore.text = "Quiz score: $quizScore"
                    binding.tvTrashscore.text = "Trash bin score: $binScore"
                    binding.tvCarbon.text = "Carboon footprint: $carbonFootprint"
                    binding.tvDividescore.text="Divide Score: $divideScore"*/
                }
                else{
                    firstInfoLoaded=true
                }
            }.addOnFailureListener {
                Toast.makeText(context, "Error nei dati", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun checkPermissionForImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_DENIED)
                && (checkSelfPermission(requireContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_DENIED)
            ) {
                val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                val permissionCoarse = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

                requestPermissions(
                    permission,
                    1001
                ) // GIVE AN INTEGER VALUE FOR PERMISSION_CODE_READ LIKE 1001
                requestPermissions(
                    permissionCoarse,
                    1002
                ) // GIVE AN INTEGER VALUE FOR PERMISSION_CODE_WRITE LIKE 1002
            } else {
                val changePicDialog= AlertDialog.Builder(context!!)
                changePicDialog.setMessage("Vuoi cambiare immagine?")
                changePicDialog.setNegativeButton("No"){dialog,_ ->
                    dialog.dismiss()
                }
                changePicDialog.setPositiveButton("Si"){dialog,_ ->
                    pickImage()
                }
                changePicDialog.show()
            }
        }
    }

    private fun setUserData(){
        binding.profileShimmer.stopShimmer()
        binding.profileShimmer.visibility = View.INVISIBLE

        loadedProfileLayout=binding.profilePageStub.inflate() as ScrollView
        propic = loadedProfileLayout!!.findViewById(R.id.img_profile)
        if(!loadDefault){
            propic.setImageBitmap(profileImg)
            propic.setOnClickListener {
                checkPermissionForImage()
            }
        }else{
            val uri = Uri.parse("android.resource://com.eco.app/drawable/default_propic")
            propic.setImageURI(uri)
        }

        tv_username = loadedProfileLayout!!.findViewById(R.id.tv_nickname)
        tv_username.setOnClickListener {
            changeUsernameDialog()
        }

        deleteAccountButton = loadedProfileLayout!!.findViewById<Button>(R.id.btn_delete_account)
        deleteAccountButton.setOnClickListener {
            deleteDialog()
        }

        loadedProfileLayout!!.findViewById<TextView>(R.id.tv_nickname).text = username
        loadedProfileLayout!!.findViewById<TextView>(R.id.tv_quizscore).text =
            "Quiz score: $quizGameScore"
        loadedProfileLayout!!.findViewById<TextView>(R.id.tv_trashscore).text =
            "Trash bin score: $trashBinGameScore"
        loadedProfileLayout!!.findViewById<TextView>(R.id.tv_carbon).text =
            "Carboon footprint: $carbonFootprintScore"
        loadedProfileLayout!!.findViewById<TextView>(R.id.tv_dividescore).text =
            "Divide score: $garbageSorterGameScore"
        if(growingTreeScore == 0L){
            loadedProfileLayout!!.findViewById<TextView>(R.id.tv_growingtree).text =
                "???"
        }else{
            loadedProfileLayout!!.findViewById<TextView>(R.id.tv_growingtree).text =
                "Growing tree score: $growingTreeScore"
        }
        loadedProfileLayout!!.findViewById<TextView>(R.id.tv_email).text =
            email.toString()



    }

    private fun changeUsernameDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        with(alertDialogBuilder){
            setTitle("Modifica username")
            setMessage("Vuoi cambiare username?")
            setPositiveButton("Si"){dialogInterface,_->
                changeUsername()
            }
            setNegativeButton("No"){dialogInterface,_->
                dialogInterface.dismiss()
            }
            show()
        }
    }

    private fun changeUsername() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        with(alertDialogBuilder) {
            setTitle("Inserisci il nuovo username")
        }
        val input = EditText(requireContext())
        with(input) {
            setHint("Username")
            inputType = InputType.TYPE_CLASS_TEXT
        }
        alertDialogBuilder.setView(input)

        alertDialogBuilder.setPositiveButton("Ok"){dialogInterface,_->
            val newUsername = input.text.toString()
            val uid = auth.uid!!
            usersReference.child(uid).child("username").setValue(newUsername).addOnSuccessListener {
                Toast.makeText(requireContext(), "Username aggiornato correttamente", Toast.LENGTH_SHORT).show()
                tv_username.setText(newUsername)
                tv_username.invalidate()
            }.addOnFailureListener{
                Toast.makeText(requireContext(), "Errore dal server", Toast.LENGTH_SHORT).show()
            }
        }
        alertDialogBuilder.setNegativeButton("Cancella"){dialogInterface,_->
            dialogInterface.dismiss()
        }
        alertDialogBuilder.show()
    }

    private fun deleteDialog(){
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        with(alertDialogBuilder){
            setTitle("Cancellazione account")
            setMessage("Stai per cancellare il tuo account, perdendo tutti i tuoi dati, sei sicuro?")
            setPositiveButton("Si"){dialogInterface,_ ->
                //Toast.makeText(requireContext(), "Ok ti cancello", Toast.LENGTH_SHORT).show()
                if(facebookAccessToken != null){
                    val token = FacebookAuthProvider.getCredential(facebookAccessToken.toString()) //todo sistemare mail nel profilo cosi da poterlo cancellare qui
                }
                deleteUser()
                dialogInterface.dismiss()
            }
            setNegativeButton("No"){dialogInterface,_ ->
                //Toast.makeText(requireContext(), "Non ti cancello", Toast.LENGTH_SHORT).show()
                dialogInterface.dismiss()
            }
        }
        alertDialogBuilder.show()
    }

    private fun deleteUser() { //TODO FARE QUELLA DI GOOGLE
        val facebookToken = facebookAccessToken?.token
        if(facebookToken != null){
            val credential = FacebookAuthProvider.getCredential(facebookToken)
            deleteFacebookUser(credential)
        }else{
            val user = Firebase.auth.currentUser
            val uid = auth.uid!!
            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            with(alertDialogBuilder) {
                setTitle("Inserisci la tua password")
            }
            val input = EditText(requireContext())
            with(input) {
                setHint("Password")
                inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
            alertDialogBuilder.setView(input)

            alertDialogBuilder.setPositiveButton("OK") { dialogInterface, _ ->
                val pwd = input.text.toString()
                val credential = EmailAuthProvider.getCredential(user!!.email.toString(), pwd)
                user!!.reauthenticate(credential)
                    .addOnSuccessListener {
                        //Toast.makeText(requireContext(), "Allright", Toast.LENGTH_SHORT).show()
                        user.delete().addOnSuccessListener {
                            deleteFromDb(uid)
                            Toast.makeText(
                                requireContext(),
                                "Account cancellato correttamente",
                                Toast.LENGTH_SHORT
                            ).show()
                            FirebaseAuth.getInstance().signOut()
                            val intent = Intent(requireContext(),HomeWindow::class.java)
                            startActivity(intent)
                        }
                            .addOnFailureListener {
                                Toast.makeText(
                                    requireContext(),
                                    "Errore nella cancellazione",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Errore nella riautenticazione", Toast.LENGTH_SHORT).show()
                    }

            }.setNegativeButton("Annulla") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            alertDialogBuilder.show()
        }

    }

    private fun deleteFacebookUser(credential: AuthCredential) {
        val user = Firebase.auth.currentUser
        val uid = auth.uid!!
        user!!.reauthenticate(credential).addOnSuccessListener {
           // Toast.makeText(requireContext(), "Allright", Toast.LENGTH_SHORT).show()
            user.delete().addOnSuccessListener {
                deleteFromDb(uid)
                Toast.makeText(
                    requireContext(),
                    "Account cancellato correttamente",
                    Toast.LENGTH_SHORT
                ).show()
                FirebaseAuth.getInstance().signOut()
                com.facebook.login.LoginManager.getInstance().logOut();
                val intent = Intent(requireContext(), HomeWindow::class.java)
                startActivity(intent)
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Errore nella cancellazione", Toast.LENGTH_SHORT)
                    .show()
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Errore nella riautenticazione ", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteFromDb(uid : String) {
        usersReference.child(uid).removeValue()
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        register.launch(intent)
    }

}