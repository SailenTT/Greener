package com.eco.app

import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.eco.app.databinding.ActivityHomeWindowBinding
import com.google.android.material.navigation.NavigationBarView
import java.text.SimpleDateFormat
import java.util.*

class HomeWindow : AppCompatActivity() {
    private lateinit var binding : ActivityHomeWindowBinding
    //COSTANTI
    //placeholders, li useremo per costruire la ui
    companion object{
        val CARTA = "carta"
        val PLASTICA = "plastica"
        val VETRO = "vetro"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeWindowBinding.inflate(layoutInflater)
        val navBar=binding.navBar

        navBar.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.item_minigames -> {
                    // Respond to navigation item 1 click
                    true
                }
                R.id.item_calendar -> {
                    // Respond to navigation item 2 click
                    true
                }
                R.id.item_calculator->{
                    // Respond to navigation item 3 click
                    true
                }
                else -> false
            }
        }

        setContentView(binding.root)
        //CODICE PER CREARE LA LISTVIEW, PRENDE I PRIMI 7 GIORNI A PARTIRE DA MO
        val dateFormat= SimpleDateFormat("EEE d MMM yyyy")
        for (i in 0..7) {
            val cal=Calendar.getInstance()
            cal.add(Calendar.DATE,i)
            Log.d("Calendar", dateFormat.format(cal.time))
        }
        }
    }

