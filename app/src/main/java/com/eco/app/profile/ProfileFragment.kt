package com.eco.app.profile

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
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
import com.eco.app.R
import com.eco.app.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var imguri : Uri
    private lateinit var UID : String
    private var loadedProfileLayout: ScrollView?=null
    private lateinit var propic: ImageView
    private var firstInfoLoaded=false
    private var quizGameScore=0L
    private var trashBinGameScore=0L
    private var garbageSorterGameScore=0L
    private var carbonFootprintScore=0L
    private var username: CharSequence=""
    private var leaderboardPosition=0
    private lateinit var profileImg:Bitmap

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

        binding.profileShimmer.startShimmer()
        auth = Firebase.auth
        val user = auth.currentUser;
        database = Firebase.database(RegisterPage.PATHTODB)
        val usersReference = database.getReference("Users")
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

    private fun getInfos(usersReference: DatabaseReference, UID: String) {
        if (auth.currentUser != null) {
            val filename = UID
            val storageReference = FirebaseStorage.getInstance("gs://ecoapp-706b8.appspot.com")
                .getReference("propics/$filename")
            val localfile = File.createTempFile("tempImage", "jpg")
            storageReference.getFile(localfile).addOnSuccessListener {
                //val resized = decodeUri(requireContext(),Uri.fromFile(localfile),230)
                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                profileImg = Bitmap.createScaledBitmap(bitmap, 400, 400, true)

                if(firstInfoLoaded){
                    setUserData()
                }
                else{
                    firstInfoLoaded=true
                }

            }.addOnFailureListener {
                firstInfoLoaded=true
                Toast.makeText(context, "Errore nella propic", Toast.LENGTH_SHORT).show()
            }

            usersReference.child(UID).get().addOnSuccessListener {
                username = it.child("username").value as CharSequence
                trashBinGameScore = it.child("bin_score").value as Long
                quizGameScore = it.child("quiz_score").value as Long
                carbonFootprintScore = it.child("carbon_footprint").value as Long
                garbageSorterGameScore = it.child("divide_score").value as Long

                if (firstInfoLoaded) {
                    setUserData()
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
        propic.setImageBitmap(profileImg)
        propic.setOnClickListener {
            checkPermissionForImage()
        }
        loadedProfileLayout!!.findViewById<TextView>(R.id.tv_nickname).text = username
        loadedProfileLayout!!.findViewById<TextView>(R.id.tv_quizscore).text =
            "Quiz score: $quizGameScore"
        loadedProfileLayout!!.findViewById<TextView>(R.id.tv_trashscore).text =
            "Trash bin score: $trashBinGameScore"
        loadedProfileLayout!!.findViewById<TextView>(R.id.tv_carbon).text =
            "Carboon footprint: $carbonFootprintScore"
        loadedProfileLayout!!.findViewById<TextView>(R.id.tv_dividescore).text =
            "Divide Score: $garbageSorterGameScore"
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        register.launch(intent)
    }

}