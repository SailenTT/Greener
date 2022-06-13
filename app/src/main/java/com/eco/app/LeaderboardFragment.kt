package com.eco.app

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.eco.app.databinding.FragmentLeaderboardBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class LeaderboardFragment : Fragment(),LeaderboardAdapter.OnItemClicked {
    private lateinit var binding: FragmentLeaderboardBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var userReference: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentLeaderboardBinding.inflate(inflater,container,false)
        database = Firebase.database(RegisterFragment.PATHTODB)
        val array = ArrayList<LeaderBoardRow>()
        setLeaderboard(array)


        //propic del tizio che in qualche modo faremo uppare
        val imageView = ImageView(requireContext())
        imageView.setImageResource(R.drawable.arrow1)
        //creazione dell'oggetto per riempire la riga
        val leaderboardrowelement = LeaderBoardRow(100,200)
        //debug ne ho messi 10 per vedere
        /*for(i in 0..10){
            array.add(i,leaderboardrowelement)
        }
        */



        return binding.root
    }

    override fun onItemClick(item: LeaderBoardRow, position: Int) {
        //TODO mettere negli extra dell'intent l'uid del profilo cliccato
       // val intent = Intent(requireContext(),ProfileActivity::class.java)
        //startActivity(intent)
    }

    fun setLeaderboard(array : ArrayList<LeaderBoardRow>){
        userReference = database.getReference("Users")
        userReference.get().addOnSuccessListener { snapshot ->
                Toast.makeText(context, "ciao", Toast.LENGTH_SHORT).show()

        }.addOnFailureListener{
            Toast.makeText(context, "error", Toast.LENGTH_SHORT).show()
        }
        val recyclerview = binding.leaderboardRecycler
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        //riempio la recycler
        val adapter = LeaderboardAdapter(array,this)
        recyclerview.adapter = adapter
    }

}