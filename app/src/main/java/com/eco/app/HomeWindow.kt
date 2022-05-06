package com.eco.app

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.eco.app.databinding.ActivityHomeWindowBinding
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
            var currentFragment: Fragment?=null

            when (item.itemId) {
                R.id.item_minigames -> {
                    currentFragment = GameSelectionFragment()
                }
                R.id.item_calendar -> {
                    currentFragment = CalendarFragment()
                }
                R.id.item_calculator -> {
                    currentFragment = CalculatorFragment()
                }
                else -> false
            }

            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(
                R.id.home_fragment_container,
                currentFragment!!
                )
            transaction.commit()

            true

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

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }
}

