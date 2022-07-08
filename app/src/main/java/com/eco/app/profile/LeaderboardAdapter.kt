package com.eco.app.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eco.app.profile.LeaderboardAdapter.LeaderboardViewHolder
import com.eco.app.databinding.LeaderboardRowBinding

class LeaderboardAdapter(private val dataSet: ArrayList<LeaderBoardRow>, private val clickListener: OnItemClicked):RecyclerView.Adapter<LeaderboardViewHolder>() {

    //inflate del layout leaderboard_row
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val binding = LeaderboardRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return LeaderboardViewHolder(binding)
    }
    //questo binda l'adapter e la recycler, prende da solo i dati del dataset
    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
       /* val quizScore = dataSet[position].quiz_score
        val trashBinScore = dataSet[position].trashbin_score
        val profileImage = dataSet[position].profile_image
*/
        var lastElement=false
        if(position==dataSet.size-1){
            lastElement=true
        }
        holder.initialize(dataSet.get(position),clickListener,lastElement)
    }


    override fun getItemCount(): Int {
        return dataSet.size
    }

    inner class LeaderboardViewHolder(val binding : LeaderboardRowBinding): RecyclerView.ViewHolder(binding.root){
        /*var quiz_score = binding.leadQuiz
        var bin_score = binding.leadBin

         */
        var position = binding.tvLeadPosition
        var username = binding.tvUsername
        var score = binding.tvScoreLeaderboard
        var propic = binding.leaderboardProfileImg
        fun initialize(item : LeaderBoardRow, action: OnItemClicked, lastElement: Boolean){
            position.text = item.position.toString()+" "
            username.text = item.username+" "
            score.text = item.score.toString()+" "
           // quiz_score.text = "Quiz score: "+item.quiz_score.toString()
           // bin_score.text = "TrasBin score: "+item.trashbin_score.toString()
            itemView.setOnClickListener{
                action.onItemClick(item,absoluteAdapterPosition)
            }

            if(lastElement){
                (itemView.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin=0
            }
        }
    }

    interface OnItemClicked{
        fun onItemClick(item : LeaderBoardRow, position: Int)
    }



}
