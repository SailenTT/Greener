package com.eco.app.carbonfootprint

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TimePicker
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.eco.app.databinding.FragmentJourneyTimeBinding

class JourneyTime : Fragment() {

    private val args : JourneyTimeArgs by navArgs()

    private lateinit var binding: FragmentJourneyTimeBinding

    //VAR "Journey Time"
    private lateinit var timePick : TimePicker
    private lateinit var btnNextJT : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentJourneyTimeBinding.inflate(inflater, container, false)

        timePick = binding.timePicker
        btnNextJT = binding.btnNextJT

        //setto property per il timePicker della sezione "Journey Mode"
        timePick.setIs24HourView(true)
        timePick.hour = 0
        timePick.minute = 0

        binding.btnBackJT.setOnClickListener{
            findNavController().navigateUp()
        }

        btnNextJT.setOnClickListener {
            var sum = calcola()
            findNavController().navigate(JourneyTimeDirections.fromPage1ToPage2(sum))
        }

        return binding.root
    }

    fun calcola() : Float {

        var sumTotale = args.sumJourneyMode

        //ora aggiungo il moltiplicatore dato dal "Journey time"
        var ore = timePick.hour
        var minuti = timePick.minute

        if(minuti>=30) {
            ore += 1 //approssimo
        }

        if(ore <= 2) { // scaglione basso
            sumTotale *= 1.5F

        }
        else if (ore in 3..5) { // scaglione intermedio
            sumTotale *= 2.5F
        }
        else { //scaglione massimo
            sumTotale *= 3.5F
        }
        return sumTotale
    }
}