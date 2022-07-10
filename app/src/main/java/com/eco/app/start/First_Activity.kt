package com.eco.app.start
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.eco.app.HomeWindow
import com.eco.app.R
import com.eco.app.databinding.ActivityFirstBinding

class First_Activity : AppCompatActivity() {

    private lateinit var binding: ActivityFirstBinding
    private lateinit var btnLog : Button
    private lateinit var btnSign : Button
    private lateinit var btnDeb : Button
    private lateinit var txtWho : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)

        binding= ActivityFirstBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGoToHomepage.setOnClickListener {
            val intent=Intent(this, HomeWindow::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }
    }



}