package com.eco.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.eco.app.databinding.FragmentCalculatorBinding
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar

class CalculatorFragment : Fragment() {

    private lateinit var binding: FragmentCalculatorBinding
    private lateinit var cardAuto : MaterialCardView
    private lateinit var cardBus : MaterialCardView
    private lateinit var cardTreno : MaterialCardView
    private lateinit var cardTaxi : MaterialCardView
    private lateinit var cardMoto : MaterialCardView
    private lateinit var cardWalk : MaterialCardView
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
        //btnProva = binding.btnProva

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

        //funzione di prova per vedere se prende i valori correttamente
        //TODO poi toglierla e inserire i dati nel calcolo finale
        /*btnProva.setOnClickListener{
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

            Snackbar.make(it, "$CO2 totale", Snackbar.LENGTH_LONG).show()
        }*/

        return binding.root
    }

}