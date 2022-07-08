package com.eco.app.profile

import android.graphics.Bitmap
import android.widget.ImageView
import com.google.android.material.imageview.ShapeableImageView

//data class per le righe della leaderboard
data class LeaderBoardRow(var position : Int, val username:String, val score:Int){
}