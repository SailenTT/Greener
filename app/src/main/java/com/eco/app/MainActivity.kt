package com.eco.app
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        /*
        val dateFormat=SimpleDateFormat("EEE d MMM yyyy")
        for (i in 0..7) {
            val cal=Calendar.getInstance()
            cal.add(Calendar.DATE,i)
            Log.d("Calendar", dateFormat.format(cal.time))
        }
        */
    }

    //check if user is logged in
    //funzione per vedere se l'utente è già loggato
    public override fun onStart() {
        super.onStart()

        //REDIRECTO AI BUTTON PER DEBUG E TESTING, NELLA VERSIONE FINALE VA ALLA LOGIN
        val intent=Intent(this,debug_activity::class.java)
        startActivity(intent)
        /*val currentUser = auth.currentUser
        if(currentUser!= null){
            Toast.makeText(this, "Logged in ", Toast.LENGTH_SHORT).show()
            val intent=Intent(this,HomeWindow::class.java)
            startActivity(intent)
        }
        else{
            Toast.makeText(this, "Not logged in ", Toast.LENGTH_SHORT).show()
            val intent=Intent(this,LoginPage::class.java)
            startActivity(intent)
        }*/
    }
}