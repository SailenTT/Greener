package com.eco.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import com.eco.app.databinding.FragmentCalculatorBinding
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar

class CalculatorFragment : Fragment() {

    var ticketShort = 0
    var ticketMedium = 0
    var ticketLong = 0

    private lateinit var binding: FragmentCalculatorBinding
    private lateinit var cardAuto : MaterialCardView
    private lateinit var cardBus : MaterialCardView
    private lateinit var cardTreno : MaterialCardView
    private lateinit var cardTaxi : MaterialCardView
    private lateinit var cardMoto : MaterialCardView
    private lateinit var cardWalk : MaterialCardView
    private lateinit var timePick : TimePicker

    private  lateinit var btnminusShort : Button
    private  lateinit var txtvalShort : TextView
    private  lateinit var btnplusShort : Button
    private  lateinit var btnminusMedium : Button
    private  lateinit var txtvalMedium : TextView
    private  lateinit var btnplusMedium : Button
    private  lateinit var btnminusLong : Button
    private  lateinit var txtvalLong : TextView
    private  lateinit var btnplusLong : Button
    //private lateinit var btnProva : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentCalculatorBinding.inflate(inflater, container, false)

        cardAuto = binding.cvAuto
        cardBus = binding.cvBus
        cardTreno = binding.cvTrain
        cardTaxi = binding.cvTaxi
        cardMoto = binding.cvMoto
        cardWalk = binding.cvWalk
        timePick = binding.timePicker

        btnminusShort = binding.btnMinusCorto
        txtvalShort = binding.txtNticketCorto
        btnplusShort = binding.btnPlusCorto
        btnminusMedium = binding.btnMinusMedio
        txtvalMedium = binding.txtNticketMedio
        btnplusMedium = binding.btnPlusMedio
        btnminusLong = binding.btnMinusLungo
        txtvalLong = binding.txtNticketLungo
        btnplusLong =binding.btnPlusLungo
        //btnProva = binding.btnProva

        //txtvalShort.text = ticketShort.toString()
        //txtvalMedium.text = ticketMedium.toString()
        //txtvalLong.text = ticketLong.toString()

        //Listener per settare checked le varie card presenti nella GridView
        cardAuto.setOnLongClickListener{
            cardAuto.isChecked = !cardAuto.isChecked
            true
        }
        cardBus.setOnLongClickListener{
            cardBus.isChecked = !cardBus.isChecked
            true
        }
        cardTreno.setOnLongClickListener{
            cardTreno.isChecked = !cardTreno.isChecked
            true
        }
        cardTaxi.setOnLongClickListener{
            cardTaxi.isChecked = !cardTaxi.isChecked
            true
        }
        cardMoto.setOnLongClickListener{
            cardMoto.isChecked = !cardMoto.isChecked
            true
        }
        cardWalk.setOnLongClickListener{
            cardWalk.isChecked = !cardWalk.isChecked
            true
        }

        //setto property per il timePicker della sezione "Journey Mode"
        timePick.setIs24HourView(true)
        timePick.hour = 0
        timePick.minute = 0


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


        //funzione di prova per vedere se prende i valori correttamente
        //TODO poi toglierla e inserire i dati nel calcolo finale
        /*btnProva.setOnClickListener{

            //test x valori cardView OK
            var CO2 = 0

            if (cardAuto.isChecked)
                CO2 += 1

            if (cardBus.isChecked)
                CO2 += 2

            if (cardTreno.isChecked)
                CO2 += 3

            if (cardTaxi.isChecked)
                CO2 += 1

            if (cardMoto.isChecked)
                CO2 += 0.5.toInt()

            if (cardWalk.isChecked)
                CO2 += 0

            //test x valori timePicker OK
            var ore = timePick.hour
            var minuti = timePick.minute

            Snackbar.make(it, "$ore, $minuti totale", Snackbar.LENGTH_LONG).show()
        }*/

        return binding.root
    }

}