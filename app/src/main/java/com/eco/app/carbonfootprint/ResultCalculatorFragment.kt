package com.eco.app.carbonfootprint

import android.R
import android.app.ActionBar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.eco.app.databinding.FragmentResultCalculatorBinding


class ResultCalculator : Fragment() {

    private val args : ResultCalculatorArgs by navArgs()

    val Tips = Array<String>(10) { "Spegni il motore al semaforo se è rosso"; "Diminuisci l'uso di plastica in casa"; "Cambia stile di alimentazione: prova con meno carne e più pesce"; "Prendi l'aereo solo se è strettamente necessario, bisogna preferire le altre alternative";"Riduci l'uso di imballaggi e gli oggetti monouso";"Anche se costano qualcosina in più, preferisci sempre gli elettrodomestici di classe A o A+";"Limita al prettamente necessario l'uso dei riscaldamenti";"Fai la raccolta differenziata (il nostro calendario può aiutarti!)";"Scegli prodotti di bellezza ecosostenibili!";"Lo sapevi che: Specialmente alcune tipologie di carne, come quelle di manzo e di agnello, emettono quantità ingenti di gas metano?";"scegli sempre (se possibile) un mezzo di trasporto a impatto ridotto: il treno sarebbe sempre da preferire all’aereo!"}
    val rnds = (0..10).random()

    private lateinit var binding: FragmentResultCalculatorBinding

    private lateinit var txt_showResult : TextView
    private lateinit var circleUtente : RelativeLayout
    private lateinit var txt_tipsUtente : TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentResultCalculatorBinding.inflate(inflater, container, false)

        txt_showResult = binding.txtResShow
        circleUtente = binding.cirlceUtente
        txt_tipsUtente = binding.txtTipsUtente

        //calcola()
        //txt_tipsUtente.text = Tips[rnds]



        return binding.root
    }

    fun calcola() {
        val AVGmondiale = 432 //110
        val punteggio_utente = args.sumFoodTracking //160

        val view: View = binding.cirlceUtente
        val layoutParams: RelativeLayout.LayoutParams = view.layoutParams as RelativeLayout.LayoutParams

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
                layoutParams.width = 20
                layoutParams.height = 20
                view.layoutParams = layoutParams
            }
            punteggio_utente in 1.0F..10.0F -> {
                txt_showResult.text = "8"
                layoutParams.width = 40
                layoutParams.height = 40
                view.layoutParams = layoutParams
            }
            punteggio_utente in 11.0F..20.0F -> {
                txt_showResult.text = "73"
                layoutParams.width = 50
                layoutParams.height = 50
                view.layoutParams = layoutParams
            }
            punteggio_utente in 21.0F..30.0F -> {
                txt_showResult.text = "176"
                layoutParams.width = 60
                layoutParams.height = 60
                view.layoutParams = layoutParams
            }
            punteggio_utente in 31.0F..40.0F -> {
                txt_showResult.text = "221"
                layoutParams.width = 70
                layoutParams.height = 70
                view.layoutParams = layoutParams
            }
            punteggio_utente in 41.0F..50.0F -> {
                txt_showResult.text = "352"
                layoutParams.width = 100
                layoutParams.height = 100
                view.layoutParams = layoutParams
            }
            punteggio_utente in 51.0F..60.0F -> {
                txt_showResult.text = "432"
                layoutParams.width = 110
                layoutParams.height = 110
                view.layoutParams = layoutParams
            }
            punteggio_utente in 61.0F..70.0F -> {
                txt_showResult.text = "584"
                layoutParams.width = 120
                layoutParams.height = 120
                view.layoutParams = layoutParams
            }
            punteggio_utente in 71.0F..80.0F -> {
                txt_showResult.text = "584"
                layoutParams.width = 140
                layoutParams.height = 140
                view.layoutParams = layoutParams
            }
            punteggio_utente in 81.0F..90.0F -> {
                txt_showResult.text = "584"
                layoutParams.width = 150
                layoutParams.height = 150
                view.layoutParams = layoutParams
            }
            punteggio_utente >= 91.0F -> {
                txt_showResult.text = "859"
                layoutParams.width = 160
                layoutParams.height = 160
                view.layoutParams = layoutParams
            }
        }

    }
}