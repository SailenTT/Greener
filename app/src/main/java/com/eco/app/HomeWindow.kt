package com.eco.app

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.airbnb.lottie.LottieAnimationView
import com.eco.app.databinding.ActivityHomeWindowBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*

class HomeWindow : AppCompatActivity() {
    private lateinit var binding : ActivityHomeWindowBinding
    private lateinit var drawer: DrawerLayout
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var toolbar: Toolbar
    private lateinit var navView: NavigationView
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var currentUser: FirebaseUser
    private var logoutActionId: Int?=null
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

        //Inserisco gli elementi base della navigation UI
        appBarConfiguration= AppBarConfiguration(setOf(R.id.GameSelectionFragment,R.id.CalendarFragment,R.id.CalculatorFragment),drawer)

        setupActionBarWithNavController(navController,appBarConfiguration)

        navView.setupWithNavController(navController)


        val navBar=binding.navBar
        navBar.setupWithNavController(navController)

        //quando il drawer viene aperto, se l'animazione di lottie è visibile, e quindi l'utente
        //non è loggato, allora faccio partire l'animazione
        drawer.addDrawerListener(object : DrawerLayout.DrawerListener{
            override fun onDrawerStateChanged(newState: Int) {}

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                val lottie=drawer.findViewById<LottieAnimationView>(R.id.lottie_stock_profile_animation)
                if(lottie.visibility==View.VISIBLE) {
                    lottie.progress = 0f
                }
            }

            override fun onDrawerClosed(drawerView: View) {}

            override fun onDrawerOpened(drawerView: View) {
                val lottie=drawer.findViewById<LottieAnimationView>(R.id.lottie_stock_profile_animation)
                if(lottie.visibility==View.VISIBLE){
                    //resetto l'animazione
                    //faccio fermare l'animazione quando arriva all'ultimo frame
                    lottie.addAnimatorUpdateListener { animation ->
                        if(animation.animatedFraction==0.99F){
                            lottie.pauseAnimation()
                        }
                    }
                    lottie.playAnimation()
                }
            }
        })

        //TODO cambiare icona logout

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

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)
        loadDrawerMenuItems()

        return true
    }


    fun loadDrawerMenuItems(){
        //TODO se l'utente è loggato mostrare la sua profile pic nel drawer_header.xml
        if(Firebase.auth.currentUser!=null){
            currentUser= Firebase.auth.currentUser!!

            logoutActionId= View.generateViewId()


            navView.menu.removeItem(R.id.login_fragment)
            navView.menu.add(0,R.id.profile_fragment,0,getString(R.string.profile_page_name)).setIcon(R.drawable.ic_person)
            navView.menu.add(0,logoutActionId!!,1,getString(R.string.logout_menu_item))
                .setIcon(R.drawable.ic_logout)
                .setOnMenuItemClickListener {
                    drawer.closeDrawer(GravityCompat.START)
                    val logoutDialog=AlertDialog.Builder(this)
                    logoutDialog.setMessage("Sei sicuro di voler effettuare il logout?")
                    logoutDialog.setNegativeButton("Annulla"){dialog,_->
                        dialog.dismiss()
                    }
                    logoutDialog.setPositiveButton("Conferma"){dialog,_->
                        dialog.dismiss()
                        val snackbar=Snackbar.make(binding.root,"Logout effettuato con successo",Snackbar.LENGTH_SHORT)
                        snackbar.anchorView=binding.navBar
                        //tasto per annullare il logout
                        snackbar.setAction("Annulla"){
                            //settare la callback della snackbar a null
                            snackbar.dismiss()
                        }
                        //quando la snackbar non è più visibile, fa il logout
                        snackbar.addCallback(object : Snackbar.Callback(){
                            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                super.onDismissed(transientBottomBar, event)
                                if(event==Snackbar.Callback.DISMISS_EVENT_TIMEOUT){
                                    Firebase.auth.signOut()
                                    invalidateOptionsMenu()
                                }
                            }
                        })
                        snackbar.show()
                    }
                    logoutDialog.show()
                    //TODO cambiare anche l'header del menù settando la profile pic

                    true
            }
        }
        else{
            navView.menu.removeItem(R.id.profile_fragment)
            if(logoutActionId!=null) {
                navView.menu.removeItem(logoutActionId!!)
            }
            navView.menu.add(0,R.id.login_fragment,1,getString(R.string.login_menu_item)).setIcon(R.drawable.ic_login)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        finish()
    }

}

