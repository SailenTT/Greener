package com.eco.app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.eco.app.databinding.ActivityIntroductorBinding
import com.eco.app.databinding.FragmentCalendarBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {

    val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
    private lateinit var txtLunedi : TextView
    private lateinit var binding : FragmentCalendarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentCalendarBinding.inflate(layoutInflater)
        txtLunedi = binding.txtLunedi;

        val cal=Calendar.getInstance()
        val dateFormat= SimpleDateFormat("EEE d MMM yyyy")
        for (i in 0..6) {
            cal.add(Calendar.DATE,i)
            Log.d("Calendar", dateFormat.format(cal.time))
        }
        txtLunedi.setText( dateFormat.format(cal.time));

        // Inflate the layout for this fragment
        return binding.root
    }
}