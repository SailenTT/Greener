package com.eco.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eco.app.LeaderboardAdapter.LeaderboardViewHolder
import com.eco.app.databinding.LeaderboardRowBinding

class LeaderboardAdapter(private val dataSet: ArrayList<leaderboardRow>):RecyclerView.Adapter<LeaderboardViewHolder>() {
    //inflate del layout leaderboard_row
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val binding = LeaderboardRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return LeaderboardViewHolder(binding)
    }
    //questo binda l'adapter e la recycler, prende da solo i dati del dataset
    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        val quizScore = dataSet[position].quiz_score
        val trashBinScore = dataSet[position].trashbin_score
        val profileImage = dataSet[position].profile_image

    }


    override fun getItemCount(): Int {
        return dataSet.size
    }

    inner class LeaderboardViewHolder(val binding : LeaderboardRowBinding): RecyclerView.ViewHolder(binding.root){

    }



}
