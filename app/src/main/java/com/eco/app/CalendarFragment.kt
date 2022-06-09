package com.eco.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //prendo il numero e il nome dei prossimi 7 giorni e li metto in una lista
        /*val listaGiorni = ArrayList<String>()
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, 1)
        for (i in 1..7) {
            val formatter = SimpleDateFormat("dd/MM/yyyy")
            val date = cal.time
            listaGiorni.add(formatter.format(date))
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }*/

        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }
}