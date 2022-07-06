package com.eco.app.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.eco.app.databinding.ActivityLeaderboardBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

//TODO eliminare questa classe (perché è stata sostituita dal suo fragment)
class Leaderboard : AppCompatActivity(), LeaderboardAdapter.OnItemClicked {
    private lateinit var binding :ActivityLeaderboardBinding
    private lateinit var adapter: LeaderboardAdapter
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Firebase.database.reference
        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //array che va riempoito con i dati della leaderboard ( tramite oggetto )
        val array = ArrayList<LeaderBoardRow>()

        val leaderboardListener = object: ValueEventListener{
            var counter = 0;
            override fun onDataChange(snapshot: DataSnapshot) {
                for(i in snapshot.children){
                    val element = snapshot.getValue<LeaderBoardRow>()
                    array.add(counter,element!!)
                    counter++;
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Errore col db", Toast.LENGTH_SHORT).show()
            }
        }
        //propic del tizio che in qualche modo faremo uppare
        //val imageView = ImageView(this)
        //imageView.setImageResource(R.drawable.arrow1)
        //creazione dell'oggetto per riempire la riga
        //val leaderboardrowelement = LeaderBoardRow(100,200,imageView)
        //debug ne ho messi 10 per vedere

        /*for(i in 0..10){
            array.add(i,leaderboardrowelement)
        }
        */
        val recyclerview = binding.leaderboardRecycler
        recyclerview.layoutManager = LinearLayoutManager(this)
        //riempio la recycler
        val adapter = LeaderboardAdapter(array,this)
        recyclerview.adapter = adapter

    }

    override fun onItemClick(item: LeaderBoardRow, position: Int) {
        //val intent = Intent(this,ProfileActivity::class.java)
        //startActivity(intent)
    }


}