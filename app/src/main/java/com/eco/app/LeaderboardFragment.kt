package com.eco.app

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.eco.app.databinding.FragmentLeaderboardBinding


class LeaderboardFragment : Fragment(),LeaderboardAdapter.OnItemClicked {
    private lateinit var binding: FragmentLeaderboardBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentLeaderboardBinding.inflate(inflater,container,false)

        //array che va riempoito con i dati della leaderboard ( tramite oggetto )
        val array = ArrayList<LeaderBoardRow>()
        //propic del tizio che in qualche modo faremo uppare
        val imageView = ImageView(requireContext())
        imageView.setImageResource(R.drawable.arrow1)
        //creazione dell'oggetto per riempire la riga
        val leaderboardrowelement = LeaderBoardRow(100,200,imageView)
        //debug ne ho messi 10 per vedere
        for(i in 0..10){
            array.add(i,leaderboardrowelement)
        }
        val recyclerview = binding.leaderboardRecycler
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        //riempio la recycler
        val adapter = LeaderboardAdapter(array,this)
        recyclerview.adapter = adapter

        return binding.root
    }

    override fun onItemClick(item: LeaderBoardRow, position: Int) {
        //TODO mettere negli extra dell'intent l'uid del profilo cliccato
        val intent = Intent(requireContext(),ProfileActivity::class.java)
        startActivity(intent)
    }

}