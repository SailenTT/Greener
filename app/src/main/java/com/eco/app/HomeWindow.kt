package com.eco.app

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.toDrawable
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
import com.google.firebase.storage.FirebaseStorage
import java.io.File
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
                if(Firebase.auth.currentUser!=null){ //se sei loggato, setta immagine nel drawer ( se la hai )
                    val filename = Firebase.auth.currentUser!!.uid
                    val storageReference = FirebaseStorage.getInstance("gs://ecoapp-706b8.appspot.com")
                        .getReference("propics/$filename")
                    val localfile = File.createTempFile("tempImage", "jpg")
                    storageReference.getFile(localfile).addOnSuccessListener {
                        val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                        val resized = Bitmap.createScaledBitmap(bitmap, 400, 400, true)
                        lottie.setImageBitmap(resized)
                    }.addOnFailureListener {
                       // Toast.makeText(baseContext, "Errore nella propic", Toast.LENGTH_SHORT).show()
                    }
                }
                if(lottie.visibility==View.VISIBLE) {
                    if(slideOffset>=0.75f&&!lottie.isAnimating){
                        lottie.playAnimation()
                    }
                    else {
                        lottie.progress = 0f
                    }
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
    }

    override fun onSupportNavigateUp(): Boolean {
        //se l'utente si trova nella schermata di risultato del quiz
        //allora, una volta premuto il tasto per tornare indietro,
        //lo faccio tornare alla schermata di selezione dei giochi
        return if(navController.currentDestination?.id==(R.id.QuizResultFragment)){
            navController.navigate(ResultQuizFragmentDirections.actionQuizResultFragmentToGameSelectionFragment())
            true
        }
        else {
            navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
        }
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
                            //settare la callback della snackbar a null //todo rivedere snackbar fa schifo
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

}

