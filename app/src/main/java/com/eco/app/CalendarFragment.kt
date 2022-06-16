package com.eco.app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.eco.app.databinding.FragmentCalendarBinding
import com.eco.app.databinding.FragmentTrashBinGameBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class CalendarFragment : Fragment() {

    val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
    private lateinit var binding : FragmentCalendarBinding
    private lateinit var day : TextView
    private lateinit var day2 : TextView
    private lateinit var day3 : TextView
    private lateinit var day4 : TextView
    private lateinit var day5 : TextView
    private lateinit var day6 : TextView
    private lateinit var day7 : TextView
    private lateinit var mese : TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //Inflate the binding for this fragment
        binding= FragmentCalendarBinding.inflate(inflater,container,false)
        day = binding.txtDay1
        day2 = binding.txtDay2
        day3 = binding.txtDay3
        day4 = binding.txtDay4
        day5 = binding.txtDay5
        day6 = binding.txtDay6
        day7 = binding.txtDay7
        mese = binding.txtMese

        //setto il mese
        val cal1= Calendar.getInstance()
        //definisco pattern per come visualizzare la data (in questo caso "MESE")
        val df = SimpleDateFormat("MMMM")
        mese.setText(df.format(cal1.time).uppercase())


        //setto i giorni della settimana
        val listaGiorni = ArrayList<String>()
        val cal= Calendar.getInstance()
        //definisco pattern per come visualizzare la data (in questo caso "giorno numero")
        val dateFormat= SimpleDateFormat("EEEE dd")

        for (i in 0..6) {
            val date = cal.time
            listaGiorni.add(dateFormat.format(date))
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }

        day.setText(listaGiorni[0])
        day2.setText(listaGiorni[1])
        day3.setText(listaGiorni[2])
        day4.setText(listaGiorni[3])
        day5.setText(listaGiorni[4])
        day6.setText(listaGiorni[5])
        day7.setText(listaGiorni[6])


        //return inflater.inflate(R.layout.fragment_calendar, container, false)
        return binding.root
    }
}