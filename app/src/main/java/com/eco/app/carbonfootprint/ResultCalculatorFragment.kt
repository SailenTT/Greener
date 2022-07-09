package com.eco.app.carbonfootprint

import android.R
import android.app.ActionBar
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.eco.app.HomeWindow
import com.eco.app.calendar.CalendarFragment
import com.eco.app.databinding.FragmentResultCalculatorBinding
import java.util.*
import kotlin.random.Random.Default.nextInt


class ResultCalculator : Fragment() {

    private val args : ResultCalculatorArgs by navArgs()

    val tips = arrayOf("Spegni il motore al semaforo se è rosso","Diminuisci l'uso di plastica in casa","Cambia stile di alimentazione: prova con meno carne e più pesce","Prendi l'aereo solo se è strettamente necessario! bisogna preferire le altre alternative","Riduci l'uso di imballaggi e gli oggetti monouso", "Anche se costano qualcosina in più, preferisci sempre gli elettrodomestici di classe A o A+","Limita al prettamente necessario l'uso dei riscaldamenti", "Fai la raccolta differenziata (il nostro calendario può aiutarti!)", "Lo sapevi che: Specialmente alcune tipologie di carne, come quelle di manzo e di agnello, emettono quantità ingenti di gas metano?", "Scegli prodotti di bellezza ecosostenibili!")
    val random: Int = Random().nextInt(10)

    private lateinit var binding: FragmentResultCalculatorBinding

    private lateinit var  sharedPref : SharedPreferences
    private lateinit var editor : SharedPreferences.Editor

    private lateinit var txt_showResult : TextView
    private lateinit var circleUtente : RelativeLayout
    private lateinit var txt_tipsUtente : TextView
    private lateinit var txt_numUtente : TextView
    private lateinit var txtinfoUtente : TextView
    private lateinit var txtco2Utente : TextView

    companion object{
        const val SHARED_PREFS = "sharedPrefsFootprint"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentResultCalculatorBinding.inflate(inflater, container, false)

        txt_showResult = binding.txtResShow
        circleUtente = binding.cirlceUtente
        txt_tipsUtente = binding.txtTipsUtente
        txt_numUtente = binding.txtNumPER
        txtinfoUtente = binding.txtInfoPER
        txtco2Utente = binding.co2Utente

        sharedPref = activity?.getSharedPreferences(CalendarFragment.SHARED_PREFS, Context.MODE_PRIVATE)!!
        editor = sharedPref.edit()

        calcola()
        txt_tipsUtente.text = tips[random]

        binding.btnRiprovaTest.setOnClickListener{
            requireContext().getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE).edit().clear().apply()

            (requireActivity() as HomeWindow).setupActionBarDestinations(2)
            findNavController().navigate(ResultCalculatorDirections.actionFromResultBackToStart())
        }

        return binding.root
    }

    fun calcola() {
        val AVGmondiale = 432 //110

        val prefs= requireContext().getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE)
        var punteggio_utente=prefs.getFloat("punteggio",0F)
        if(punteggio_utente==0F) {
            punteggio_utente = args.sumFoodTracking //160
            (requireActivity() as HomeWindow).setupActionBarDestinations(2)
        }

            //AVGmondiale (dato preso da internet) è 432 con punteggio di 60
            //il calcolo funzionerà quindi con un compare del punteggio utente su questo valore;

            /* scala valore
           0 -> 0 kg 20
           1-10 -> 8 kg 40
           11 - 20 -> 73 kg 50
           21 - 30 -> 176 kg 60
           31 - 40 -> 221 kg 70
           41 - 50 -> 352 kg 100
           51 - 60 -> 432 kg 110
           61 - 70 -> 584 kg 120
           71 - 80 -> 657 kg 140
           81 - 90 -> 749 kg 150
           91 + -> 859 kg 160
        */

        when {
            punteggio_utente == 0.0F -> {
                txt_showResult.text = "0"
                txt_numUtente.text = "0"
                circleUtente.layoutParams.width =
                    (20 * resources.displayMetrics.density).toInt()
                circleUtente.layoutParams.height =
                    (20 * resources.displayMetrics.density).toInt()
                txt_numUtente.textSize = 10F
                txtinfoUtente.textSize = 8F
                txtco2Utente.textSize = 8F
                saveSharedPref(
                    punteggio_utente
                )
            }
            punteggio_utente in 1.0F..10.0F -> {
                txt_showResult.text = "8"
                txt_numUtente.text = "8"
                circleUtente.layoutParams.width =
                    (40 * resources.displayMetrics.density).toInt()
                circleUtente.layoutParams.height =
                    (40 * resources.displayMetrics.density).toInt()
                txt_numUtente.textSize = 10F
                txtinfoUtente.textSize = 8F
                txtco2Utente.textSize = 8F
                saveSharedPref(
                    punteggio_utente
                )

            }
            punteggio_utente in 11.0F..20.0F -> {
                txt_showResult.text = "73"
                txt_numUtente.text = "73"
                circleUtente.layoutParams.width =
                    (50 * resources.displayMetrics.density).toInt()
                circleUtente.layoutParams.height =
                    (50 * resources.displayMetrics.density).toInt()
                txt_numUtente.textSize = 10F
                txtinfoUtente.textSize = 8F
                txtco2Utente.textSize = 8F
                saveSharedPref(
                    punteggio_utente
                )

            }
            punteggio_utente in 21.0F..30.0F -> {
                txt_showResult.text = "176"
                txt_numUtente.text = "176"
                circleUtente.layoutParams.width =
                    (60 * resources.displayMetrics.density).toInt()
                circleUtente.layoutParams.height =
                    (60 * resources.displayMetrics.density).toInt()
                txt_numUtente.textSize = 11F
                txtinfoUtente.textSize = 9F
                txtco2Utente.textSize = 9F
                saveSharedPref(
                    punteggio_utente
                )

            }
            punteggio_utente in 31.0F..40.0F -> {
                txt_showResult.text = "221"
                txt_numUtente.text = "221"
                circleUtente.layoutParams.width =
                    (70 * resources.displayMetrics.density).toInt()
                circleUtente.layoutParams.height =
                    (70 * resources.displayMetrics.density).toInt()
                txt_numUtente.textSize = 11F
                txtinfoUtente.textSize = 9F
                txtco2Utente.textSize = 9F
                saveSharedPref(
                    punteggio_utente
                )

            }
            punteggio_utente in 41.0F..50.0F -> {
                txt_showResult.text = "352"
                txt_numUtente.text = "352"
                circleUtente.layoutParams.width =
                    (100 * resources.displayMetrics.density).toInt()
                circleUtente.layoutParams.height =
                    (100 * resources.displayMetrics.density).toInt()
                txt_numUtente.textSize = 11F
                txtinfoUtente.textSize = 9F
                txtco2Utente.textSize = 9F
                saveSharedPref(
                    punteggio_utente
                )

            }
            punteggio_utente in 51.0F..60.0F -> {
                txt_showResult.text = "432"
                txt_numUtente.text = "432"
                circleUtente.layoutParams.width =
                    (110 * resources.displayMetrics.density).toInt()
                circleUtente.layoutParams.height =
                    (110 * resources.displayMetrics.density).toInt()
                txtinfoUtente.textSize = 12F
                txtco2Utente.textSize = 12F
                saveSharedPref(
                    punteggio_utente
                )

            }
            punteggio_utente in 61.0F..70.0F -> {
                txt_showResult.text = "584"
                txt_numUtente.text = "584"
                circleUtente.layoutParams.width =
                    (120 * resources.displayMetrics.density).toInt()
                circleUtente.layoutParams.height =
                    (120 * resources.displayMetrics.density).toInt()
                txtinfoUtente.textSize = 12F
                txtco2Utente.textSize = 12F
                saveSharedPref(
                    punteggio_utente
                )

            }
            punteggio_utente in 71.0F..80.0F -> {
                txt_showResult.text = "657"
                txt_numUtente.text = "657"
                circleUtente.layoutParams.width =
                    (140 * resources.displayMetrics.density).toInt()
                circleUtente.layoutParams.height =
                    (140 * resources.displayMetrics.density).toInt()
                txtinfoUtente.textSize = 14F
                txtco2Utente.textSize = 14F
                saveSharedPref(
                    punteggio_utente
                )

            }
            punteggio_utente in 81.0F..90.0F -> {
                txt_showResult.text = "749"
                txt_numUtente.text = "749"
                circleUtente.layoutParams.width =
                    (150 * resources.displayMetrics.density).toInt()
                circleUtente.layoutParams.height =
                    (150 * resources.displayMetrics.density).toInt()
                txtinfoUtente.textSize = 16F
                txtco2Utente.textSize = 16F
                saveSharedPref(
                    punteggio_utente
                )

            }
            punteggio_utente >= 91.0F -> {
                txt_showResult.text = "859"
                txt_numUtente.text = "859"
                circleUtente.layoutParams.width =
                    (160 * resources.displayMetrics.density).toInt()
                circleUtente.layoutParams.height =
                    (160 * resources.displayMetrics.density).toInt()
                txtinfoUtente.textSize = 18F
                txtco2Utente.textSize = 18F
                saveSharedPref(
                    punteggio_utente
                )
            }
        }
        /*else{
            val width=sharedPref.getInt("width", 0)
            val height=sharedPref.getInt("height", 0)
            val scoreSize=sharedPref.getFloat("scoreSize",0F)
            val textSize=sharedPref.getFloat("textSize",0F)
            txt_showResult.text = punteggio_utente.toString()
            txt_numUtente.text = punteggio_utente.toString()
            circleUtente.layoutParams.width =width
            circleUtente.layoutParams.height =height
            txt_numUtente.textSize = scoreSize
            txtinfoUtente.textSize = textSize
            txtco2Utente.textSize = textSize
        }*/
    }

    fun saveSharedPref(punteggio : Float/*, width : Int, height : Int, scoreSize : Float, textSize : Float*/) {

        val sharedPref = activity?.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPref?.edit()
        editor?.apply {

            putFloat("punteggio",punteggio)
            /*putInt("width", width)
            putInt("height", height)
            putFloat("scoreSize",scoreSize)
            putFloat("textSize",textSize)*/

        }?.apply()
    }
}