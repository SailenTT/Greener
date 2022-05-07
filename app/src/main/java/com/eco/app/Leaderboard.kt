package com.eco.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.eco.app.databinding.ActivityLeaderboardBinding

class Leaderboard : AppCompatActivity(),LeaderboardAdapter.OnItemClicked {
    private lateinit var binding :ActivityLeaderboardBinding
    private lateinit var adapter: LeaderboardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)
        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //array che va riempoito con i dati della leaderboard ( tramite oggetto )
        val array = ArrayList<LeaderBoardRow>()
        //propic del tizio che in qualche modo faremo uppare
        val imageView = ImageView(this)
        imageView.setImageResource(R.drawable.arrow1)
        //creazione dell'oggetto per riempire la riga
        val leaderboardrowelement = LeaderBoardRow(100,200,imageView)
        //debug ne ho messi 10 per vedere
        for(i in 0..10){
            array.add(i,leaderboardrowelement)
        }
        val recyclerview = binding.leaderboardRecycler
        recyclerview.layoutManager = LinearLayoutManager(this)
        //riempio la recycler
        val adapter = LeaderboardAdapter(array,this)
        recyclerview.adapter = adapter

    }

    override fun onItemClick(item: LeaderBoardRow, position: Int) {
        val intent = Intent(this,ProfileActivity::class.java)
        startActivity(intent)
    }


}