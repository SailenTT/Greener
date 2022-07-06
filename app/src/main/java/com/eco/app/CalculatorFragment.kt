package com.eco.app

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.eco.app.databinding.FragmentCalculatorBinding
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar


class CalculatorFragment : Fragment() {

    var JourneyModeScore = 0 //variabile che tiene conto del punteggio dell'utente nella pagina "Journey Mode";
    //il punteggio va in base a che mezzo usa l'utente per muoversi, su una scala da 1 a 10. (0 non c'è perchè anche camminando un minimo si inquina)
    //1 = walk
    //3 = moto
    //5 = auto (trasportando più passeggeri di una moto inquina di più)
    //5 = taxi ("")
    //8 = bus (vale lo stesso concetto, più passeggeri si porta più si inquina)
    //10 = treno (valore massimo al mezzo che trasporta più persone (escluso aereo perchè ha una sezione propria))

    var JourneyTime = 0 //variabile che conterrà quanto tempo l'utente ha passato in viaggio su uno dei mezzi scelti
    //verrà usato come moltiplicatore in scaglioni

    var ticketShort = 0 //contatore ticket S
    //tenendo conto che il treno prima ha preso il punteggio massimo a 10
    //per ogni biglietto corto vengono aggiunti 15 punti (30 per andata e ritorno)

    var ticketMedium = 0 //contatore ticket M
    //per ogni biglietto medio vengono aggiunti 20 punti (40 per andata e ritorno)

    var ticketLong = 0 //contatore ticket L
    //per ogni biglietto lungo vengono aggiunti 25 punti (50 per andata e ritorno)

    var whoIsChecked = 0 //var che tiene conto di quale checkbox è true

    var FoodTrackingScore = 0 //variabile che tiene conto del punteggio dell'utente nella pagina "Food Tracking";
    //anche per questo, il punteggio va in base a che dieta (mediamente) segue l'utente. anche qui abbiamo una divisione in scala decrescente:
    //10 punti a carnePlus
    //8 punti a carneMedia
    //6 punti a carneLow
    //4 punti a pescetarian
    //3 punti a vetetarian
    //2 punti a veggy
    //il punteggio è basato su quanto la tua dieta possa produrre CO2 (con allevamenti, produzione etc)

    private lateinit var binding: FragmentCalculatorBinding

    //VAR "Journey Mode"
    private lateinit var cardAuto : MaterialCardView
    private lateinit var cardBus : MaterialCardView
    private lateinit var cardTreno : MaterialCardView
    private lateinit var cardTaxi : MaterialCardView
    private lateinit var cardMoto : MaterialCardView
    private lateinit var cardWalk : MaterialCardView
    private lateinit var btnNextJM : Button

    //VAR "Journey Time"
    private lateinit var timePick : TimePicker
    private lateinit var btnPrevJT : Button
    private lateinit var btnNextJT : Button

    //VAR "Your Flights"
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

    //VAR "Food Tracking"
    private lateinit var checkMeatPlus : CheckBox
    private lateinit var checkMeat : CheckBox
    private lateinit var checkMeatLow : CheckBox
    private lateinit var checkFish : CheckBox
    private lateinit var checkVeget : CheckBox
    private lateinit var checkVeggy : CheckBox
    private lateinit var btnPrevFT : Button
    private lateinit var btnNextFT : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentCalculatorBinding.inflate(inflater, container, false)

        //BINDING "Journey Mode"
        cardAuto = binding.cvAuto
        cardBus = binding.cvBus
        cardTreno = binding.cvTrain
        cardTaxi = binding.cvTaxi
        cardMoto = binding.cvMoto
        cardWalk = binding.cvWalk
        btnNextJM = binding.btnNextJM

        //BINDING "Journey Time"
        timePick = binding.timePicker
        btnPrevJT = binding.btnPrevJT
        btnNextJT = binding.btnNextJT

        //BINDING "Your Flights"
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

        //BINDING "Food Tracking"
        checkMeatPlus = binding.checkMeatplus
        checkMeat = binding.checkMeat
        checkMeatLow = binding.checkMeatlow
        checkFish = binding.checkFish
        checkVeget = binding.checkVeget
        checkVeggy = binding.checkVeggy
        btnPrevFT = binding.btnPrevFT
        btnNextFT = binding.btnNextFT

        //Listener per settare checked le varie card presenti nella GridView
        cardAuto.setOnClickListener {
            if (cardAuto.isChecked == false) {
                cardAuto.isChecked = true
                JourneyModeScore += 5 //aggiorno valore dello score
            } else {
                cardAuto.isChecked = false
                JourneyModeScore = 0 //nel caso tolga la spunta rimuovo il valore aggiunto
            }
        }

        cardBus.setOnClickListener {
            if (cardBus.isChecked == false) {
                cardBus.isChecked = true
                JourneyModeScore += 8
            } else {
                cardBus.isChecked = false
                JourneyModeScore = 0
            }
        }

        cardTreno.setOnClickListener {
            if (cardTreno.isChecked == false) {
                cardTreno.isChecked = true
                JourneyModeScore += 10
            } else {
                cardTreno.isChecked = false
                JourneyModeScore = 0
            }
        }

        cardTaxi.setOnClickListener {
            if (cardTaxi.isChecked == false) {
                cardTaxi.isChecked = true
                JourneyModeScore += 5
            } else {
                cardTaxi.isChecked = false
                JourneyModeScore = 0
            }
        }

        cardMoto.setOnClickListener {
            if (cardMoto.isChecked == false) {
                cardMoto.isChecked = true
                JourneyModeScore += 3
            } else {
                cardMoto.isChecked = false
                JourneyModeScore = 0
            }
        }

        cardWalk.setOnClickListener {
            if (cardWalk.isChecked == false) {
                cardWalk.isChecked = true
                JourneyModeScore += 1
            } else {
                cardWalk.isChecked = false
                JourneyModeScore = 0
            }
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

        //Listener per le checkbox del "food tracker"
        checkMeatPlus.setOnClickListener {
            if (checkMeatPlus.isChecked) {
                //se la checkbox è selezionata salvo in whoIsChecked (0 la prima, ..., 5 l'ultima)
                //dopo setto tutte le altre false in modo tale che solo una possa essere selezionabile
                whoIsChecked = 0
                FoodTrackingScore += 10
                checkMeat.isChecked = false
                checkMeatLow.isChecked = false
                checkFish.isChecked = false
                checkVeget.isChecked = false
                checkVeggy.isChecked = false
            }
        }

        checkMeat.setOnClickListener {
            if (checkMeat.isChecked) {
                whoIsChecked = 1
                FoodTrackingScore += 8
                checkMeatPlus.isChecked = false
                checkMeatLow.isChecked = false
                checkFish.isChecked = false
                checkVeget.isChecked = false
                checkVeggy.isChecked = false
            }
        }

        checkMeatLow.setOnClickListener {
            if (checkMeatLow.isChecked) {
                whoIsChecked = 2
                FoodTrackingScore += 6
                checkMeatPlus.isChecked = false
                checkMeat.isChecked = false
                checkFish.isChecked = false
                checkVeget.isChecked = false
                checkVeggy.isChecked = false
            }
        }

        checkFish.setOnClickListener {
            if (checkFish.isChecked) {
                whoIsChecked = 3
                FoodTrackingScore += 4
                checkMeatPlus.isChecked = false
                checkMeat.isChecked = false
                checkMeatLow.isChecked = false
                checkVeget.isChecked = false
                checkVeggy.isChecked = false
            }
        }

        checkVeget.setOnClickListener {
            if (checkVeget.isChecked) {
                whoIsChecked = 4
                FoodTrackingScore += 3
                checkMeatPlus.isChecked = false
                checkMeat.isChecked = false
                checkMeatLow.isChecked = false
                checkFish.isChecked = false
                checkVeggy.isChecked = false
            }
        }

        checkVeggy.setOnClickListener {
            if (checkVeggy.isChecked) {
                whoIsChecked = 5
                FoodTrackingScore += 2
                checkMeatPlus.isChecked = false
                checkMeat.isChecked = false
                checkMeatLow.isChecked = false
                checkFish.isChecked = false
                checkVeget.isChecked = false
            }
        }

        //funzione di prova per vedere se prende i valori correttamente
        //TODO poi toglierla e inserire i dati nel calcolo finale
        btnNextJT.setOnClickListener{

            var sum = calcolaCO2()

            Snackbar.make(it, "$sum totale", Snackbar.LENGTH_LONG).show()
        }

        return binding.root
    }

    fun  calcolaCO2(): Double {
        var sumTotale : Double = 0.0

        //per prima cosa aggiungo il punteggio del "Journey Mode"
        sumTotale += JourneyModeScore

        //ora aggiungo il moltiplicatore dato dal "Journey time"
        var ore = timePick.hour
        var minuti = timePick.minute

        if(minuti>=30) {
            ore += 1 //approssimo
        }

        if(ore <= 2) { // scaglione basso
            sumTotale *= 1.5

        }
        else if (ore in 3..5) { // scaglione intermedio
            sumTotale *= 2.5
        }
        else { //scaglione massimo
            sumTotale *= 3.5
        }

        //ora aggiungo la parte di your flight


        return sumTotale.toDouble()
    }

}