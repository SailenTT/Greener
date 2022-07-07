package com.eco.app.carbonfootprint

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.eco.app.R
import com.eco.app.databinding.FragmentYourFlightsBinding


class YourFlights : Fragment() {

    var ticketShort = 0 //contatore ticket S
    //tenendo conto che il treno prima ha preso il punteggio massimo a 10
    //per ogni biglietto corto vengono aggiunti 15 punti (30 per andata e ritorno)

    var ticketMedium = 0 //contatore ticket M
    //per ogni biglietto medio vengono aggiunti 20 punti (40 per andata e ritorno)

    var ticketLong = 0 //contatore ticket L
    //per ogni biglietto lungo vengono aggiunti 25 punti (50 per andata e ritorno)

    private lateinit var binding : FragmentYourFlightsBinding

    private  lateinit var btnminusShort : Button
    private  lateinit var txtvalShort : TextView
    private  lateinit var btnplusShort : Button

    private  lateinit var btnminusMedium : Button
    private  lateinit var txtvalMedium : TextView
    private  lateinit var btnplusMedium : Button

    private  lateinit var btnminusLong : Button
    private  lateinit var txtvalLong : TextView
    private  lateinit var btnplusLong : Button

    private lateinit var btnPrevYF : Button
    private lateinit var btnNextYF : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentYourFlightsBinding.inflate(inflater, container, false)

        btnminusShort = binding.btnMinusCorto
        txtvalShort = binding.txtNticketCorto
        btnplusShort = binding.btnPlusCorto

        btnminusMedium = binding.btnMinusMedio
        txtvalMedium = binding.txtNticketMedio
        btnplusMedium = binding.btnPlusMedio

        btnminusLong = binding.btnMinusLungo
        txtvalLong = binding.txtNticketLungo
        btnplusLong = binding.btnPlusLungo

        btnPrevYF = binding.btnPrevYF
        btnNextYF = binding.btnNextYF

        //Listener dei btn per la sezione "VOLI"
        btnminusShort.setOnClickListener {
            ticketShort--

            //controllo del valore:
            //se va negativo faccio tornare a 0 cosi da avere sempre un valore positivo.
            if (ticketShort<0)
                ticketShort = 0

            txtvalShort.text = ticketShort.toString()
        }

        btnplusShort.setOnClickListener {
            ticketShort++
            txtvalShort.text = ticketShort.toString()
        }

        btnminusMedium.setOnClickListener {
            ticketMedium--

            if (ticketMedium<0)
                ticketMedium = 0

            txtvalMedium.text = ticketMedium.toString()
        }

        btnplusMedium.setOnClickListener {
            ticketMedium++
            txtvalMedium.text = ticketMedium.toString()
        }

        btnminusLong.setOnClickListener {
            ticketLong--

            if (ticketLong<0)
                ticketLong = 0

            txtvalLong.text = ticketLong.toString()
        }

        btnplusLong.setOnClickListener {
            ticketLong++
            txtvalLong.text = ticketLong.toString()
        }

        return binding.root
    }

    fun calcola() : Double {

        var sumTotale : Double = 0.0

        sumTotale += (ticketShort * 15.0) + (ticketMedium * 20.0) + (ticketLong * 25.0)

        return sumTotale
    }
}