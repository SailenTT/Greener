package com.eco.app.carbonfootprint

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.eco.app.R
import com.eco.app.databinding.FragmentFoodTrackingBinding
import com.eco.app.databinding.FragmentYourFlightsBinding

class FoodTracking : Fragment() {

    private val args : FoodTrackingArgs by navArgs()

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

    private lateinit var binding: FragmentFoodTrackingBinding

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

        binding = FragmentFoodTrackingBinding.inflate(inflater, container, false)

        checkMeatPlus = binding.checkMeatplus
        checkMeat = binding.checkMeat
        checkMeatLow = binding.checkMeatlow
        checkFish = binding.checkFish
        checkVeget = binding.checkVeget
        checkVeggy = binding.checkVeggy

        btnPrevFT = binding.btnPrevFT
        btnNextFT = binding.btnNextFT

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

        btnNextFT.setOnClickListener {
            var sum = calcola()
            findNavController().navigate(FoodTrackingDirections.fromPage3ToResultPage(sum))
        }

        return binding.root
    }

    fun calcola() : Float {

        var sumTotale : Float = args.sumYourFlights

        when (whoIsChecked) {
            0 -> {
                sumTotale += 10.0F
            }
            1 -> {
                sumTotale += 8.0F
            }
            2 -> {
                sumTotale += 6.0F
            }
            3 -> {
                sumTotale += 4.0F
            }
            4 -> {
                sumTotale += 3.0F
            }
            5 -> {
                sumTotale += 2.0F
            }
        }

        return sumTotale
    }
}