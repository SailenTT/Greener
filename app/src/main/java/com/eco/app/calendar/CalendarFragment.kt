package com.eco.app.calendar

import android.app.*
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.eco.app.R
import com.eco.app.databinding.FragmentCalendarBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class CalendarFragment : Fragment() {

    val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
    private lateinit var binding : FragmentCalendarBinding
    private lateinit var calendar : Calendar
    private lateinit var alarmManager : AlarmManager

    private lateinit var  sharedPref : SharedPreferences
    private lateinit var editor : SharedPreferences.Editor
    private lateinit var day : TextView
    private lateinit var day2 : TextView
    private lateinit var day3 : TextView
    private lateinit var day4 : TextView
    private lateinit var day5 : TextView
    private lateinit var day6 : TextView
    private lateinit var day7 : TextView
    private lateinit var mese : TextView
    private lateinit var btnData : Button
    private lateinit var switch : Switch
    private lateinit var bottomSliderContainer : RelativeLayout
    private lateinit var doubleArrowIcon: ImageView
    private lateinit var pendingIntent : PendingIntent

    companion object{
        const val SHARED_PREFS = "sharedPrefsCalendar"
    }

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
        switch = binding.notificationSwitch

        sharedPref = activity?.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)!!
        editor = sharedPref.edit()


        setMonth()
        setWeekDays()
        setSharedPref()
        setCircleColor()
        createNotificationChannel()

        switch.isChecked = false //valore iniziale dello switch, sarà off
        editor.putString("isChecked","false")

        if(sharedPref.getString("isChecked","0") == "true"){
            switch.isChecked = true
            scheduleNotifications()
        }else{
            switch.isChecked = false
        }



        switch.setOnCheckedChangeListener { _, isChecked ->
            //if(Firebase.auth.currentUser != null){
                if(isChecked){ //registra gli eventi dello switch, capisco se lo ha lasciato on o off, cosi quando riapre la activity lo ritrova come prima
                    editor.putString("isChecked","true")
                    editor.commit()
                    scheduleNotifications()
                    //Toast.makeText(context, "Notifiche attivate", Toast.LENGTH_SHORT).show()
                }else{
                    editor.putString("isChecked","false")
                    editor.commit()
                    unScheduleNotifications()
                }
            //}else{
              //  Toast.makeText(context, "Devi essere loggato", Toast.LENGTH_SHORT).show()
           // }

        }

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

    private fun unScheduleNotifications() {
        alarmManager = context!!.getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationCalendar::class.java)

        pendingIntent = PendingIntent.getBroadcast(context,0,intent,0)

        alarmManager.cancel(pendingIntent)
       // Toast.makeText(context, "Notifiche disabilitate", Toast.LENGTH_SHORT).show()
    }

    private fun scheduleNotifications(){

        calendar = Calendar.getInstance() //getto istanza del calendario
        calendar[Calendar.HOUR_OF_DAY] = 13  //setto orario,minuti secondi e millisecondi di quando deve arrivare la notifica
        calendar[Calendar.MINUTE] = 47
        calendar[Calendar.SECOND] = 30
        calendar[Calendar.MILLISECOND] = 0

        alarmManager = context!!.getSystemService(ALARM_SERVICE) as AlarmManager //getto il servizio di alarm manager
        val intent = Intent(context, NotificationCalendar::class.java)
        intent.putExtra("day",calendar[Calendar.DAY_OF_WEEK])
        //creo un intent per reindirizzarlo al receiver

        pendingIntent = PendingIntent.getBroadcast(context,0,intent,0) //pendingIntent dato che non viene eseguito subito, notificationCalendar è un receiver

        alarmManager.setRepeating( //uso alarmmanager per settare i parametri
            AlarmManager.RTC_WAKEUP,calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,pendingIntent
        )
        //Toast.makeText(context, "Alarm settato", Toast.LENGTH_SHORT)



    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "CalendarNotificationChannel1"
            val descriptionText = "Questo è il canale per le notifiche riguardo a cosa portare fuori e quando"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("notificationCalendarChannel", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context!!.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }



    fun setMonth(){
        //setto il mese
        val cal1= Calendar.getInstance()
        //definisco pattern per come visualizzare la data (in questo caso "MESE")
        val df = SimpleDateFormat("MMMM")
        mese.text = df.format(cal1.time).uppercase()
    }
    fun setWeekDays(){
        //setto i giorni della settimana
        val listaGiorni = ArrayList<String>()
        val cal= Calendar.getInstance()
        //definisco pattern per come visualizzare la data (in questo caso "giorno numero")
        val dateFormat= SimpleDateFormat("EEEE") //dd

        for (i in 0..6) {
            val date = cal.time
            listaGiorni.add(dateFormat.format(date))
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }

        day.text = listaGiorni[0]
        day2.text = listaGiorni[1]
        day3.text = listaGiorni[2]
        day4.text = listaGiorni[3]
        day5.text = listaGiorni[4]
        day6.text = listaGiorni[5]
        day7.text = listaGiorni[6]
    }




    fun setSharedPref(){

        val rifiutoD1 : TextView = binding.day1Rifiuto
        val rifiutoD2 : TextView = binding.day2Rifiuto
        val rifiutoD3 : TextView = binding.day3Rifiuto
        val rifiutoD4 : TextView = binding.day4Rifiuto
        val rifiutoD5 : TextView = binding.day5Rifiuto
        val rifiutoD6 : TextView = binding.day6Rifiuto
        val rifiutoD7 : TextView = binding.day7Rifiuto



        //TODO se vuoi rimettere i numeri vai alla riga 200 e metti (EEEE dd)
        //però se vuoi di nuovo i numeri devi trovare un modo per mettere il contains qui
        //perchè day (con il numero) sarebbe "martedi 10"
        when (day.text) {
            "lunedì" -> {
                rifiutoD1.text = sharedPref.getString("lunedi","")
                rifiutoD2.text = sharedPref.getString("martedi","")
                rifiutoD3.text = sharedPref.getString("mercoledi","")
                rifiutoD4.text = sharedPref.getString("giovedi","")
                rifiutoD5.text = sharedPref.getString("venerdi","")
                rifiutoD6.text = sharedPref.getString("sabato","")
                rifiutoD7.text = sharedPref.getString("domenica","")
            }
            "martedì" -> {
                rifiutoD1.text = sharedPref.getString("martedi","")
                rifiutoD2.text = sharedPref.getString("mercoledi","")
                rifiutoD3.text = sharedPref.getString("giovedi","")
                rifiutoD4.text = sharedPref.getString("venerdi","")
                rifiutoD5.text = sharedPref.getString("sabato","")
                rifiutoD6.text = sharedPref.getString("domenica","")
                rifiutoD7.text = sharedPref.getString("lunedi","")
            }
            "mercoledì" -> {
                rifiutoD1.text = sharedPref.getString("mercoledi","")
                rifiutoD2.text = sharedPref.getString("giovedi","")
                rifiutoD3.text = sharedPref.getString("venerdi","")
                rifiutoD4.text = sharedPref.getString("sabato","")
                rifiutoD5.text = sharedPref.getString("domenica","")
                rifiutoD6.text = sharedPref.getString("lunedi","")
                rifiutoD7.text = sharedPref.getString("martedi","")
            }
            "giovedì" -> {
                rifiutoD1.text = sharedPref.getString("giovedi","")
                rifiutoD2.text = sharedPref.getString("venerdi","")
                rifiutoD3.text = sharedPref.getString("sabato","")
                rifiutoD4.text = sharedPref.getString("domenica","")
                rifiutoD5.text = sharedPref.getString("lunedi","")
                rifiutoD6.text = sharedPref.getString("martedi","")
                rifiutoD7.text = sharedPref.getString("mercoledi","")
            }
            "venerdì" -> {
                rifiutoD1.text = sharedPref.getString("venerdi","")
                rifiutoD2.text = sharedPref.getString("sabato","")
                rifiutoD3.text = sharedPref.getString("domenica","")
                rifiutoD4.text = sharedPref.getString("lunedi","")
                rifiutoD5.text = sharedPref.getString("martedi","")
                rifiutoD6.text = sharedPref.getString("mercoledi","")
                rifiutoD7.text = sharedPref.getString("giovedi","")
            }
            "sabato" -> {
                rifiutoD1.text = sharedPref.getString("sabato","")
                rifiutoD2.text = sharedPref.getString("domenica","")
                rifiutoD3.text = sharedPref.getString("lunedi","")
                rifiutoD4.text = sharedPref.getString("martedi","")
                rifiutoD5.text = sharedPref.getString("mercoledi","")
                rifiutoD6.text = sharedPref.getString("giovedi","")
                rifiutoD7.text = sharedPref.getString("venerdi","")
            }
            "domenica" -> {
                rifiutoD1.text = sharedPref.getString("domenica","")
                rifiutoD2.text = sharedPref.getString("lunedi","")
                rifiutoD3.text = sharedPref.getString("martedi","")
                rifiutoD4.text = sharedPref.getString("mercoledi","")
                rifiutoD5.text = sharedPref.getString("giovedi","")
                rifiutoD6.text = sharedPref.getString("venerdi","")
                rifiutoD7.text = sharedPref.getString("sabato","")
            }
        }


        /*if (day.text.toString().contains("lunedì")) {
            rifiutoD1.setText(sharedPref.getString("lunedi",""))
            rifiutoD2.setText(sharedPref.getString("martedi",""))
            rifiutoD3.setText(sharedPref.getString("mercoledi",""))
            rifiutoD4.setText(sharedPref.getString("giovedi",""))
            rifiutoD5.setText(sharedPref.getString("venerdi",""))
            rifiutoD6.setText(sharedPref.getString("sabato",""))
            rifiutoD7.setText(sharedPref.getString("domenica",""))
        } else if (day.text.toString().contains("martedì")){
            rifiutoD1.setText(sharedPref.getString("martedi",""))
            rifiutoD2.setText(sharedPref.getString("mercoledi",""))
            rifiutoD3.setText(sharedPref.getString("giovedi",""))
            rifiutoD4.setText(sharedPref.getString("venerdi",""))
            rifiutoD5.setText(sharedPref.getString("sabato",""))
            rifiutoD6.setText(sharedPref.getString("domenica",""))
            rifiutoD7.setText(sharedPref.getString("lunedi",""))
        } else if (day.text.toString().contains("mercoledì")){
            rifiutoD1.setText(sharedPref.getString("mercoledi",""))
            rifiutoD2.setText(sharedPref.getString("giovedi",""))
            rifiutoD3.setText(sharedPref.getString("venerdi",""))
            rifiutoD4.setText(sharedPref.getString("sabato",""))
            rifiutoD5.setText(sharedPref.getString("domenica",""))
            rifiutoD6.setText(sharedPref.getString("lunedi",""))
            rifiutoD7.setText(sharedPref.getString("martedi",""))
        } else if (day.text.toString().contains("giovedì")){
            rifiutoD1.setText(sharedPref.getString("giovedi",""))
            rifiutoD2.setText(sharedPref.getString("venerdi",""))
            rifiutoD3.setText(sharedPref.getString("sabato",""))
            rifiutoD4.setText(sharedPref.getString("domenica",""))
            rifiutoD5.setText(sharedPref.getString("lunedi",""))
            rifiutoD6.setText(sharedPref.getString("martedi",""))
            rifiutoD7.setText(sharedPref.getString("mercoledi",""))
        } else if (day.text.toString().contains("venerdi")){
            rifiutoD1.setText(sharedPref.getString("venerdi",""))
            rifiutoD2.setText(sharedPref.getString("sabato",""))
            rifiutoD3.setText(sharedPref.getString("domenica",""))
            rifiutoD4.setText(sharedPref.getString("lunedi",""))
            rifiutoD5.setText(sharedPref.getString("martedi",""))
            rifiutoD6.setText(sharedPref.getString("mercoledi",""))
            rifiutoD7.setText(sharedPref.getString("giovedi",""))
        } else if (day.text.toString().contains("sabato")){
            rifiutoD1.setText(sharedPref.getString("sabato",""))
            rifiutoD2.setText(sharedPref.getString("domenica",""))
            rifiutoD3.setText(sharedPref.getString("lunedi",""))
            rifiutoD4.setText(sharedPref.getString("martedi",""))
            rifiutoD5.setText(sharedPref.getString("mercoledi",""))
            rifiutoD6.setText(sharedPref.getString("giovedi",""))
            rifiutoD7.setText(sharedPref.getString("venerdi",""))
        } else {
            rifiutoD1.setText(sharedPref.getString("domenica",""))
            rifiutoD2.setText(sharedPref.getString("lunedi",""))
            rifiutoD3.setText(sharedPref.getString("martedi",""))
            rifiutoD4.setText(sharedPref.getString("mercoledi",""))
            rifiutoD5.setText(sharedPref.getString("giovedi",""))
            rifiutoD6.setText(sharedPref.getString("venerdi",""))
            rifiutoD7.setText(sharedPref.getString("sabato",""))
        }*/
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.whitePopulationContainer.post{
            //setto il container sotto lo schermo così l'utente può tirarlo su premendo
            binding.doubleArrowWidgetContainer.y=0f
            bottomSliderContainer.y+=binding.whitePopulationContainer.height.toFloat()
            binding.doubleArrowWidgetContainer.rotation=0f


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
}