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
    private val daysList=listOf("lunedì","martedì","mercoledì","giovedì","venerdì","sabato","domenica")
    private val daysListSharedPrefsForm= listOf("lunedi","martedi","mercoledi","giovedi","venerdi","sabato","domenica")
    private val wastesList= mutableListOf<TextView>()

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
        calendar[Calendar.HOUR_OF_DAY] = 12 //setto orario,minuti secondi e millisecondi di quando deve arrivare la notifica
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0

        alarmManager = context!!.getSystemService(ALARM_SERVICE) as AlarmManager //getto il servizio di alarm manager
        val intent = Intent(context, NotificationCalendar::class.java)
        //intent.putExtra("day",calendar[Calendar.DAY_OF_WEEK])
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
        val dateFormat= SimpleDateFormat("EEEE dd") //dd

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

        wastesList.add(0,binding.day1Rifiuto)
        wastesList.add(1,binding.day2Rifiuto)
        wastesList.add(2,binding.day3Rifiuto)
        wastesList.add(3,binding.day4Rifiuto)
        wastesList.add(4,binding.day5Rifiuto)
        wastesList.add(5,binding.day6Rifiuto)
        wastesList.add(6,binding.day7Rifiuto)

        var i=0
        while(i <daysList.size) {
            if (day.text.toString().contains(daysList[i])) {
                break
            }
            else {
                i++
            }
        }

        for(j in 0 until wastesList.size) {
            wastesList[j].text= sharedPref.getString(daysListSharedPrefsForm[i],"")
            println(sharedPref.getString(daysListSharedPrefsForm[i],""))
            i++
            if(i==daysList.size){
                i=0
            }
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



        for(i in calendarDays.indices){
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
                "Indifferenziata"->{
                    cDs[i].setImageResource(R.drawable.indiff_day_icon)
                }
                "--"->{
                    cDs[i].setImageDrawable(null)
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

            changeSliderMenuPosition()
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
                changeSliderMenuPosition()
            }
        }
    }

    fun changeSliderMenuPosition(){
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