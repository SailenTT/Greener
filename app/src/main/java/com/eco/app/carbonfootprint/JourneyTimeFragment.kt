package com.eco.app.carbonfootprint

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.Button
import android.widget.TimePicker
import com.eco.app.R
import com.eco.app.databinding.ActivityDebugBinding.inflate
import com.eco.app.databinding.ActivityFirstBinding.inflate
import com.eco.app.databinding.FragmentCalculatorBinding
import com.eco.app.databinding.FragmentJourneyTimeBinding

class JourneyTime : Fragment() {

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }*/
    private lateinit var binding: FragmentJourneyTimeBinding

    //VAR "Journey Time"
    private lateinit var timePick : TimePicker
    private lateinit var btnPrevJT : Button
    private lateinit var btnNextJT : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentJourneyTimeBinding.inflate(inflater, container, false)

        timePick = binding.timePicker
        btnPrevJT = binding.btnPrevJT
        btnNextJT = binding.btnNextJT

        //setto property per il timePicker della sezione "Journey Mode"
        timePick.setIs24HourView(true)
        timePick.hour = 0
        timePick.minute = 0

        return binding.root
    }

    fun calcola() : Double {

        var sumTotale : Double = 0.0

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
        return sumTotale
    }
}