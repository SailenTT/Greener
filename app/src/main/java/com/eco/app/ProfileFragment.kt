package com.eco.app

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import com.eco.app.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage : FirebaseStorage
    private lateinit var imguri : Uri
    private lateinit var UID : String
    val register = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            //Toast.makeText(requireContext(), "APPOSTO REGISTER LAUNCHER", Toast.LENGTH_SHORT).show()
            imguri = it.data?.data!!
            val imgBitmap : Bitmap? = context?.let { it1 -> decodeUri(it1,imguri,230) }
            binding.imgProfile.setImageBitmap(imgBitmap)
            uploadToStorage(UID)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentProfileBinding.inflate(inflater,container,false)
        auth = Firebase.auth
        val user = auth.currentUser;
        database = Firebase.database(RegisterPage.PATHTODB)
        val usersReference = database.getReference("Users")
        if(user==null){
            Log.i("LoginInfo","Non sei loggato")
        }else{
             UID = user.uid
            getInfos(usersReference,UID)
            binding.imgProfile.setOnClickListener {
                checkPermissionForImage()
            }
        }

        return binding.root
    }

    fun uploadToStorage(UID : String){
        val filename = UID
        val storageReference = FirebaseStorage.getInstance("gs://ecoapp-706b8.appspot.com").getReference("propics/$filename")
        storageReference.putFile(imguri).addOnSuccessListener {
            Toast.makeText(context, "YES", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(context, "FUCK", Toast.LENGTH_SHORT).show()
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

    private fun getInfos(usersReference : DatabaseReference, UID : String) { //todo finire con regole aperte
        usersReference.child(UID).get().addOnSuccessListener {
            val username : CharSequence = it.child("username").value as CharSequence
            val binScore : Long= it.child("bin_score").value as Long
            val quizScore : Long = it.child("quiz_score").value as Long
            val carbonFootprint : Long = it.child("carbon_footprint").value as Long
            binding.tvName.text = username
            binding.tvQuizscore1.text = quizScore.toString()
            binding.tvTrashscore1.text = binScore.toString()
            binding.tvCarbon1.text = carbonFootprint.toString()

        }.addOnFailureListener{
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
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
                pickImage()
            }
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        register.launch(intent)
    }

}