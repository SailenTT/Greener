package com.eco.app

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavInflater
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.eco.app.databinding.ActivityHomeWindowBinding
import com.google.android.material.navigation.NavigationView
import java.text.SimpleDateFormat
import java.util.*

class HomeWindow : AppCompatActivity() {
    private lateinit var binding : ActivityHomeWindowBinding
    private lateinit var drawer: DrawerLayout
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var toolbar: Toolbar
    private lateinit var navView: NavigationView
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerToggle: ActionBarDrawerToggle
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
        setContentView(binding.root)

        drawer=binding.root
        toolbar=binding.toolbar
        navView=binding.navView
        navHostFragment=supportFragmentManager.findFragmentById(R.id.home_fragment_container) as NavHostFragment
        navController=navHostFragment.navController

        setSupportActionBar(binding.toolbar)

        appBarConfiguration= AppBarConfiguration(setOf(R.id.GameSelectionFragment,R.id.CalendarFragment,R.id.CalculatorFragment),drawer)

        setupActionBarWithNavController(navController,appBarConfiguration)

        navView.setupWithNavController(navController)


        val navBar=binding.navBar
        navBar.setupWithNavController(navController)

        /*
        //CODICE PER CREARE LA LISTVIEW, PRENDE I PRIMI 7 GIORNI A PARTIRE DA MO
        //PER CRI
        val dateFormat= SimpleDateFormat("EEE d MMM yyyy")
        for (i in 0..7) {
            val cal=Calendar.getInstance()
            cal.add(Calendar.DATE,i)
            Log.d("Calendar", dateFormat.format(cal.time))
        }*/
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }
}

