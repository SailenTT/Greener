package com.eco.app.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.eco.app.R
import com.eco.app.databinding.FragmentLeaderboardBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File


class LeaderboardFragment : Fragment(), LeaderboardAdapter.OnItemClicked {
    private var arrayList = ArrayList<String>()
    private lateinit var binding: FragmentLeaderboardBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var userReference: DatabaseReference
    private lateinit var getUsersDataListener: ValueEventListener
    private var firstInfoLoaded=false
    private var scoreList  = mutableListOf<Int>()
    private var usernameList  = mutableListOf<String>()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentLeaderboardBinding.inflate(inflater,container,false)
        database = Firebase.database(getString(R.string.path_to_db))
        binding.leaderboardShimmer.startShimmer()

        val array = ArrayList<LeaderBoardRow>()
        setLeaderboard(array)

        //creazione dell'oggetto per riempire la riga



        return binding.root
    }

    override fun onItemClick(item: LeaderBoardRow, position: Int) {
        //TODO mettere negli extra dell'intent l'uid del profilo cliccato e scegliere se visualizzare profilo
       // val intent = Intent(requireContext(),ProfileActivity::class.java)
        //startActivity(intent)
    }

    fun setLeaderboard(array : ArrayList<LeaderBoardRow>){
        userReference = database.getReference("Users")
        getUsersDataListener = userReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var pos = 1
                for (i in snapshot.children){
                    //SCARICO PROPICS
                    val uid = i.key!!
                    val localfile = File.createTempFile("leaderboardImage$i", "jpg")
                    val storageRef = FirebaseStorage.getInstance("gs://ecoapp-706b8.appspot.com").getReference("propics/$uid")

                    var profileImg : Bitmap?

                    arrayList.add(uid)
                    val quizScore = i.child("quiz_score").getValue(Long::class.java)!!.toInt()
                    val trashScore = i.child("bin_score").getValue(Long::class.java)!!.toInt()
                    val divideScore = i.child("divide_score").getValue(Long::class.java)!!.toInt()
                    val carbonFoot = i.child("carbon_footprint").getValue(Long::class.java)!!.toInt()
                    scoreList.add(quizScore+trashScore+divideScore+carbonFoot)
                    usernameList .add(i.child("username").getValue(String::class.java)!!)

                    storageRef.getFile(localfile).addOnSuccessListener {
                        val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                        profileImg = Bitmap.createScaledBitmap(bitmap, 400, 400, true)

                        val leaderboardrow = LeaderBoardRow(profileImg!!,pos,usernameList[pos-1],scoreList[pos-1])
                        array.add(leaderboardrow)
                        pos++
                        //verifico se è l'ultima immagine della lista
                        if(pos>snapshot.children.count()){
                            sortArray(array)
                            val recyclerview = binding.leaderboardRecycler
                            recyclerview.layoutManager = LinearLayoutManager(requireContext())
                            //riempio la recycler
                            val adapter = LeaderboardAdapter(array,this@LeaderboardFragment)
                            recyclerview.adapter = adapter
                            if(binding.leaderboardShimmer.isShimmerStarted){
                                binding.leaderboardShimmer.stopShimmer()
                                binding.leaderboardShimmer.visibility = View.INVISIBLE
                                //binding.leaderboardRelativelayout.visibility = View.VISIBLE
                            }
                        }

                    }.addOnFailureListener {
                        firstInfoLoaded=true
                        //Toast.makeText(context, "Errore nella propic", Toast.LENGTH_SHORT).show()
                        val uri = Uri.parse("android.resource://com.eco.app/drawable/default_propic")
                        profileImg = MediaStore.Images.Media.getBitmap(requireContext().contentResolver,uri)

                        val leaderboardrow = LeaderBoardRow(profileImg!!,pos,usernameList[pos-1],scoreList[pos-1])
                        array.add(leaderboardrow)
                        pos++
                        //verifico se è l'ultima immagine della lista
                        if(pos>snapshot.children.count()){
                            sortArray(array)
                            val recyclerview = binding.leaderboardRecycler
                            recyclerview.layoutManager = LinearLayoutManager(requireContext())
                            //riempio la recycler
                            val adapter = LeaderboardAdapter(array,this@LeaderboardFragment)
                            recyclerview.adapter = adapter
                            if(binding.leaderboardShimmer.isShimmerStarted){
                                binding.leaderboardShimmer.stopShimmer()
                                binding.leaderboardShimmer.visibility = View.INVISIBLE
                                //binding.leaderboardRelativelayout.visibility = View.VISIBLE
                            }
                        }
                    }

                    /*if(firstInfoLoaded) {
                        val leaderboardrow = LeaderBoardRow(profileImg!!,pos,username,score)
                        array.add(leaderboardrow)
                        firstInfoLoaded=false
                    }
                    else{
                        firstInfoLoaded=true
                    }*/
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


    }

    private fun sortArray(array: ArrayList<LeaderBoardRow>) {
        array.sortByDescending { l1 -> l1.score }
        for(i in array.indices){
            array[i].position = i+1
        }
    }

    override fun onDestroy() {
        userReference.removeEventListener(getUsersDataListener)
        super.onDestroy()
    }



}