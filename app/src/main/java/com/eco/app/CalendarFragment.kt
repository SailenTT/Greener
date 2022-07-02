package com.eco.app

import android.app.AlertDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.marginTop
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
    private lateinit var bottomSliderContainer : RelativeLayout
    private lateinit var doubleArrowIcon: ImageView
    private var dpi: Float=0f
    private var dialogBuilder: AlertDialog.Builder? = null
    private var dialog: AlertDialog? = null
    private lateinit var calendarDays: List<TextView>


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        //Inflate the binding for this fragment
        binding= FragmentCalendarBinding.inflate(inflater,container,false)

        calendarDays= listOf(binding.day1Rifiuto,binding.day2Rifiuto,binding.day3Rifiuto,binding.day4Rifiuto,binding.day5Rifiuto,binding.day6Rifiuto,binding.day7Rifiuto)

        doubleArrowIcon=binding.doubleArrowWidget

        bottomSliderContainer=binding.bottomSliderContainer

        dpi=resources.displayMetrics.density

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
        setCircleColor()

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

        val rifiutoD1 : TextView = binding.day1Rifiuto
        val rifiutoD2 : TextView = binding.day2Rifiuto
        val rifiutoD3 : TextView = binding.day3Rifiuto
        val rifiutoD4 : TextView = binding.day4Rifiuto
        val rifiutoD5 : TextView = binding.day5Rifiuto
        val rifiutoD6 : TextView = binding.day6Rifiuto
        val rifiutoD7 : TextView = binding.day7Rifiuto

        val sharedPref = activity?.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        if (day.text.toString().contains("lunedì")) {
            rifiutoD1.setText(sharedPref!!.getString("lunedi",""))
            rifiutoD2.setText(sharedPref.getString("martedi",""))
            rifiutoD3.setText(sharedPref.getString("mercoledi",""))
            rifiutoD4.setText(sharedPref.getString("giovedi",""))
            rifiutoD5.setText(sharedPref.getString("venerdi",""))
            rifiutoD6.setText(sharedPref.getString("sabato",""))
            rifiutoD7.setText(sharedPref.getString("domenica",""))
        } else if (day.text.toString().contains("martedì")){
            rifiutoD1.setText(sharedPref!!.getString("martedi",""))
            rifiutoD2.setText(sharedPref.getString("mercoledi",""))
            rifiutoD3.setText(sharedPref.getString("giovedi",""))
            rifiutoD4.setText(sharedPref.getString("venerdi",""))
            rifiutoD5.setText(sharedPref.getString("sabato",""))
            rifiutoD6.setText(sharedPref.getString("domenica",""))
            rifiutoD7.setText(sharedPref.getString("lunedi",""))
        } else if (day.text.toString().contains("mercoledì")){
            rifiutoD1.setText(sharedPref!!.getString("mercoledi",""))
            rifiutoD2.setText(sharedPref.getString("giovedi",""))
            rifiutoD3.setText(sharedPref.getString("venerdi",""))
            rifiutoD4.setText(sharedPref.getString("sabato",""))
            rifiutoD5.setText(sharedPref.getString("domenica",""))
            rifiutoD6.setText(sharedPref.getString("lunedi",""))
            rifiutoD7.setText(sharedPref.getString("martedi",""))
        } else if (day.text.toString().contains("giovedì")){
            rifiutoD1.setText(sharedPref!!.getString("giovedi",""))
            rifiutoD2.setText(sharedPref.getString("venerdi",""))
            rifiutoD3.setText(sharedPref.getString("sabato",""))
            rifiutoD4.setText(sharedPref.getString("domenica",""))
            rifiutoD5.setText(sharedPref.getString("lunedi",""))
            rifiutoD6.setText(sharedPref.getString("martedi",""))
            rifiutoD7.setText(sharedPref.getString("mercoledi",""))
        } else if (day.text.toString().contains("venerdi")){
            rifiutoD1.setText(sharedPref!!.getString("venerdi",""))
            rifiutoD2.setText(sharedPref.getString("sabato",""))
            rifiutoD3.setText(sharedPref.getString("domenica",""))
            rifiutoD4.setText(sharedPref.getString("lunedi",""))
            rifiutoD5.setText(sharedPref.getString("martedi",""))
            rifiutoD6.setText(sharedPref.getString("mercoledi",""))
            rifiutoD7.setText(sharedPref.getString("giovedi",""))
        } else if (day.text.toString().contains("sabato")){
            rifiutoD1.setText(sharedPref!!.getString("sabato",""))
            rifiutoD2.setText(sharedPref.getString("domenica",""))
            rifiutoD3.setText(sharedPref.getString("lunedi",""))
            rifiutoD4.setText(sharedPref.getString("martedi",""))
            rifiutoD5.setText(sharedPref.getString("mercoledi",""))
            rifiutoD6.setText(sharedPref.getString("giovedi",""))
            rifiutoD7.setText(sharedPref.getString("venerdi",""))
        } else {
            rifiutoD1.setText(sharedPref!!.getString("domenica",""))
            rifiutoD2.setText(sharedPref.getString("lunedi",""))
            rifiutoD3.setText(sharedPref.getString("martedi",""))
            rifiutoD4.setText(sharedPref.getString("mercoledi",""))
            rifiutoD5.setText(sharedPref.getString("giovedi",""))
            rifiutoD6.setText(sharedPref.getString("venerdi",""))
            rifiutoD7.setText(sharedPref.getString("sabato",""))
        }
    }
    fun setCircleColor() {
        val cD1 : View = binding.circleD1
        val cD2 : View = binding.circleD2
        val cD3 : View = binding.circleD3
        val cD4 : View = binding.circleD4
        val cD5 : View = binding.circleD5
        val cD6 : View = binding.circleD6
        val cD7 : View = binding.circleD7
        val cDs = listOf(cD1 as ImageView, cD2 as ImageView, cD3 as ImageView, cD4 as ImageView, cD5 as ImageView, cD6 as ImageView, cD7 as ImageView)

        //TODO cambiare sta roba
        /*if (binding.day1Rifiuto.text.equals("Vetro")){ cD1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#47844E")))}
        else if(binding.day1Rifiuto.text.equals("Carta")){ cD1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#32A4F4")))}
        else if(binding.day1Rifiuto.text.equals("Umido")){ cD1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#695206")))}
        else if(binding.day1Rifiuto.text.equals("Plastica")){ cD1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EBDC37")))}
        else if(binding.day1Rifiuto.text.equals("Ingombranti")){ cD1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#d64d2e")))}
        else if(binding.day1Rifiuto.text.equals("Indifferenziato")){ cD1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#a8a8a8")))}
        else if(binding.day1Rifiuto.text.equals("--")){ cD1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#eaeaea")))}

        if (binding.day2Rifiuto.text.equals("Vetro")){ cD2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#47844E")))}
        else if(binding.day2Rifiuto.text.equals("Carta")){ cD2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#32A4F4")))}
        else if(binding.day2Rifiuto.text.equals("Umido")){ cD2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#695206")))}
        else if(binding.day2Rifiuto.text.equals("Plastica")){ cD2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EBDC37")))}
        else if(binding.day2Rifiuto.text.equals("Ingombranti")){ cD2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#d64d2e")))}
        else if(binding.day2Rifiuto.text.equals("Indifferenziato")){ cD2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#a8a8a8")))}
        else if(binding.day2Rifiuto.text.equals("--")){ cD2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#eaeaea")))}

        if (binding.day3Rifiuto.text.equals("Vetro")){ cD3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#47844E")))}
        else if(binding.day3Rifiuto.text.equals("Carta")){ cD3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#32A4F4")))}
        else if(binding.day3Rifiuto.text.equals("Umido")){ cD3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#695206")))}
        else if(binding.day3Rifiuto.text.equals("Plastica")){ cD3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EBDC37")))}
        else if(binding.day3Rifiuto.text.equals("Ingombranti")){ cD3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#d64d2e")))}
        else if(binding.day3Rifiuto.text.equals("Indifferenziato")){ cD3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#a8a8a8")))}
        else if(binding.day3Rifiuto.text.equals("--")){ cD3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#eaeaea")))}

        if (binding.day4Rifiuto.text.equals("Vetro")){ cD4.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#47844E")))}
        else if(binding.day4Rifiuto.text.equals("Carta")){ cD4.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#32A4F4")))}
        else if(binding.day4Rifiuto.text.equals("Umido")){ cD4.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#695206")))}
        else if(binding.day4Rifiuto.text.equals("Plastica")){ cD4.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EBDC37")))}
        else if(binding.day4Rifiuto.text.equals("Ingombranti")){ cD4.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#d64d2e")))}
        else if(binding.day4Rifiuto.text.equals("Indifferenziato")){ cD4.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#a8a8a8")))}
        else if(binding.day4Rifiuto.text.equals("--")){ cD4.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#eaeaea")))}

        if (binding.day5Rifiuto.text.equals("Vetro")){ cD5.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#47844E")))}
        else if(binding.day5Rifiuto.text.equals("Carta")){ cD5.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#32A4F4")))}
        else if(binding.day5Rifiuto.text.equals("Umido")){ cD5.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#695206")))}
        else if(binding.day5Rifiuto.text.equals("Plastica")){ cD5.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EBDC37")))}
        else if(binding.day5Rifiuto.text.equals("Ingombranti")){ cD5.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#d64d2e")))}
        else if(binding.day5Rifiuto.text.equals("Indifferenziato")){ cD5.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#a8a8a8")))}
        else if(binding.day5Rifiuto.text.equals("--")){ cD5.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#eaeaea")))}

        if (binding.day6Rifiuto.text.equals("Vetro")){ cD6.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#47844E")))}
        else if(binding.day6Rifiuto.text.equals("Carta")){ cD6.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#32A4F4")))}
        else if(binding.day6Rifiuto.text.equals("Umido")){ cD6.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#695206")))}
        else if(binding.day6Rifiuto.text.equals("Plastica")){ cD6.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EBDC37")))}
        else if(binding.day6Rifiuto.text.equals("Ingombranti")){ cD6.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#d64d2e")))}
        else if(binding.day6Rifiuto.text.equals("Indifferenziato")){ cD6.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#a8a8a8")))}
        else if(binding.day6Rifiuto.text.equals("--")){ cD6.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#eaeaea")))}

        if (binding.day7Rifiuto.text.equals("Vetro")){ cD7.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#47844E")))}
        else if(binding.day7Rifiuto.text.equals("Carta")){ cD7.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#32A4F4")))}
        else if(binding.day7Rifiuto.text.equals("Umido")){ cD7.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#695206")))}
        else if(binding.day7Rifiuto.text.equals("Plastica")){ cD7.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EBDC37")))}
        else if(binding.day7Rifiuto.text.equals("Ingombranti")){ cD7.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#d64d2e")))}
        else if(binding.day7Rifiuto.text.equals("Indifferenziato")){ cD7.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#a8a8a8")))}
        else if(binding.day7Rifiuto.text.equals("--")){ cD7.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#eaeaea")))}*/
        for(i in 0..6){
            when(calendarDays[i].text){
                "Vetro" -> {
                    cDs[i].setImageResource(R.drawable.glass_day_icon)
                }
                "Carta"->{
                    cDs[i].setImageResource(R.drawable.paper_day_icon)
                }
                "Umido"->{
                    cDs[i].setImageResource(R.drawable.organic_day_icon)
                }
                "Plastica"->{
                    cDs[i].setImageResource(R.drawable.plastic_day_icon)
                }
                "Ingombranti"->{
                    cDs[i].setImageResource(R.drawable.bulky_day_icon)
                }
                "Indifferenziato"->{
                    cDs[i].setImageResource(R.drawable.indiff_day_icon)
                }
            }
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
            setCircleColor()
        })
    }

    override fun onResume() {
        super.onResume()

        //setto il container sotto lo schermo così l'utente può tirarlo su premendo
        binding.doubleArrowWidgetContainer.y=0f
        if(bottomSliderContainer.y+binding.whitePopulationContainer.y<binding.root.height) {
            bottomSliderContainer.y+=binding.whitePopulationContainer.height.toFloat()
            binding.doubleArrowWidgetContainer.rotation=0f
        }


        binding.doubleArrowWidgetContainer.setOnClickListener {
            if(bottomSliderContainer.y+binding.whitePopulationContainer.y>=binding.root.height) {
                //slide in alto
                bottomSliderContainer.animate()
                    .translationYBy(-binding.whitePopulationContainer.height.toFloat())
                    .duration = 450

                binding.doubleArrowWidgetContainer.animate()
                    .translationYBy(5f*dpi)

            }
            else{
                //slide in basso
                bottomSliderContainer.animate()
                    .translationYBy(binding.whitePopulationContainer.height.toFloat())
                    .duration = 450

                binding.doubleArrowWidgetContainer.animate()
                    .translationYBy(-5f*dpi)
            }

            doubleArrowIcon.animate()
                .rotationBy(180f)
                .duration=460
        }
    }
}