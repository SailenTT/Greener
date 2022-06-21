package com.eco.app

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.eco.app.databinding.FragmentCalendarBinding
import com.eco.app.databinding.FragmentPopupBinding
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
    private lateinit var btnData : Button
    private var dialogBuilder: AlertDialog.Builder? = null
    private var dialog: AlertDialog? = null


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
        btnData = binding.btnDataCalendar

        setMonth()
        setWeekDays()
        setSharedPref()

        btnData.setOnClickListener(View.OnClickListener {

            dialogBuilder = AlertDialog.Builder(context)
            val contactPopupView = layoutInflater.inflate(R.layout.fragment_popup, null)

            dialogBuilder!!.setView(contactPopupView)
            dialog = dialogBuilder!!.create()
            dialog?.show()

            saveSharedPref()
        })

        return binding.root
    }

    fun setMonth(){
        //setto il mese
        val cal1= Calendar.getInstance()
        //definisco pattern per come visualizzare la data (in questo caso "MESE")
        val df = SimpleDateFormat("MMMM")
        mese.setText(df.format(cal1.time).uppercase())
    }
    fun setWeekDays(){
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
    }
    fun setSharedPref(){
        val SHARED_PREFS = "sharedPrefsCalendar"

        val rifiutoD1 : TextView
        val rifiutoD2 : TextView
        val rifiutoD3 : TextView
        val rifiutoD4 : TextView
        val rifiutoD5 : TextView
        val rifiutoD6 : TextView
        val rifiutoD7 : TextView

        rifiutoD1 = binding.day1Rifiuto
        rifiutoD2 = binding.day2Rifiuto
        rifiutoD3 = binding.day3Rifiuto
        rifiutoD4 = binding.day4Rifiuto
        rifiutoD5 = binding.day5Rifiuto
        rifiutoD6 = binding.day6Rifiuto
        rifiutoD7 = binding.day7Rifiuto

        val sharedPref = activity?.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        if (day.text.toString().contains("lunedì")) {
            rifiutoD1.setText(sharedPref!!.getString("lunedi",""))
            rifiutoD2.setText(sharedPref!!.getString("martedi",""))
            rifiutoD3.setText(sharedPref!!.getString("mercoledi",""))
            rifiutoD4.setText(sharedPref!!.getString("giovedi",""))
            rifiutoD5.setText(sharedPref!!.getString("venerdi",""))
            rifiutoD6.setText(sharedPref!!.getString("sabato",""))
            rifiutoD7.setText(sharedPref!!.getString("domenica",""))
        } else if (day.text.toString().contains("martedì")){
            rifiutoD1.setText(sharedPref!!.getString("martedi",""))
            rifiutoD2.setText(sharedPref!!.getString("mercoledi",""))
            rifiutoD3.setText(sharedPref!!.getString("giovedi",""))
            rifiutoD4.setText(sharedPref!!.getString("venerdi",""))
            rifiutoD5.setText(sharedPref!!.getString("sabato",""))
            rifiutoD6.setText(sharedPref!!.getString("domenica",""))
            rifiutoD7.setText(sharedPref!!.getString("lunedi",""))
        } else if (day.text.toString().contains("mercoledì")){
            rifiutoD1.setText(sharedPref!!.getString("mercoledi",""))
            rifiutoD2.setText(sharedPref!!.getString("giovedi",""))
            rifiutoD3.setText(sharedPref!!.getString("venerdi",""))
            rifiutoD4.setText(sharedPref!!.getString("sabato",""))
            rifiutoD5.setText(sharedPref!!.getString("domenica",""))
            rifiutoD6.setText(sharedPref!!.getString("lunedi",""))
            rifiutoD7.setText(sharedPref!!.getString("martedi",""))
        } else if (day.text.toString().contains("giovedì")){
            rifiutoD1.setText(sharedPref!!.getString("giovedi",""))
            rifiutoD2.setText(sharedPref!!.getString("venerdi",""))
            rifiutoD3.setText(sharedPref!!.getString("sabato",""))
            rifiutoD4.setText(sharedPref!!.getString("domenica",""))
            rifiutoD5.setText(sharedPref!!.getString("lunedi",""))
            rifiutoD6.setText(sharedPref!!.getString("martedi",""))
            rifiutoD7.setText(sharedPref!!.getString("mercoledi",""))
        } else if (day.text.toString().contains("venerdi")){
            rifiutoD1.setText(sharedPref!!.getString("venerdi",""))
            rifiutoD2.setText(sharedPref!!.getString("sabato",""))
            rifiutoD3.setText(sharedPref!!.getString("domenica",""))
            rifiutoD4.setText(sharedPref!!.getString("lunedi",""))
            rifiutoD5.setText(sharedPref!!.getString("martedi",""))
            rifiutoD6.setText(sharedPref!!.getString("mercoledi",""))
            rifiutoD7.setText(sharedPref!!.getString("giovedi",""))
        } else if (day.text.toString().contains("sabato")){
            rifiutoD1.setText(sharedPref!!.getString("sabato",""))
            rifiutoD2.setText(sharedPref!!.getString("domenica",""))
            rifiutoD3.setText(sharedPref!!.getString("lunedi",""))
            rifiutoD4.setText(sharedPref!!.getString("martedi",""))
            rifiutoD5.setText(sharedPref!!.getString("mercoledi",""))
            rifiutoD6.setText(sharedPref!!.getString("giovedi",""))
            rifiutoD7.setText(sharedPref!!.getString("venerdi",""))
        } else {
            rifiutoD1.setText(sharedPref!!.getString("domenica",""))
            rifiutoD2.setText(sharedPref!!.getString("lunedi",""))
            rifiutoD3.setText(sharedPref!!.getString("martedi",""))
            rifiutoD4.setText(sharedPref!!.getString("mercoledi",""))
            rifiutoD5.setText(sharedPref!!.getString("giovedi",""))
            rifiutoD6.setText(sharedPref!!.getString("venerdi",""))
            rifiutoD7.setText(sharedPref!!.getString("sabato",""))
        }
    }
    fun saveSharedPref(){

        val SHARED_PREFS = "sharedPrefsCalendar"

        var rifiutoLunedi : Spinner
        var rifiutoMartedi : Spinner
        var rifiutoMercoledi : Spinner
        var rifiutoGiovedi : Spinner
        var rifiutoVenerdi : Spinner
        var rifiutoSabato : Spinner
        var rifiutoDomenica : Spinner
        var btnPopola : Button
        var prova : TextView

        rifiutoLunedi = dialog!!.findViewById(R.id.spin_day1)
        rifiutoMartedi = dialog!!.findViewById(R.id.spin_day2)
        rifiutoMercoledi = dialog!!.findViewById(R.id.spin_day3)
        rifiutoGiovedi = dialog!!.findViewById(R.id.spin_day4)
        rifiutoVenerdi = dialog!!.findViewById(R.id.spin_day5)
        rifiutoSabato = dialog!!.findViewById(R.id.spin_day6)
        rifiutoDomenica = dialog!!.findViewById(R.id.spin_day7)
        btnPopola = dialog!!.findViewById(R.id.btn_popInvio)

        btnPopola.setOnClickListener(View.OnClickListener {

            val sharedPref = activity?.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
            val editor = sharedPref?.edit()
            editor?.apply {

                putString("lunedi",rifiutoLunedi.selectedItem.toString())
                putString("martedi",rifiutoMartedi.selectedItem.toString())
                putString("mercoledi",rifiutoMercoledi.selectedItem.toString())
                putString("giovedi",rifiutoGiovedi.selectedItem.toString())
                putString("venerdi",rifiutoVenerdi.selectedItem.toString())
                putString("sabato",rifiutoSabato.selectedItem.toString())
                putString("domenica",rifiutoDomenica.selectedItem.toString())

            }?.apply()
            dialog?.dismiss()
            setSharedPref()
        })
    }
}