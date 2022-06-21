package com.eco.app

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.eco.app.databinding.FragmentPopupBinding

class PopupFragment : Fragment() {

    private lateinit var binding: FragmentPopupBinding
    private lateinit var rifiutoLunedi : Spinner
    private lateinit var rifiutoMartedi : Spinner
    private lateinit var rifiutoMercoledi : Spinner
    private lateinit var rifiutoGiovedi : Spinner
    private lateinit var rifiutoVenerdi : Spinner
    private lateinit var rifiutoSabato : Spinner
    private lateinit var rifiutoDomenica : Spinner
    private lateinit var btnPopola : Button

    private var dialogBuilder: AlertDialog.Builder? = null
    private var dialog: AlertDialog? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPopupBinding.inflate(inflater,container,false)

        rifiutoLunedi = binding.spinDay1
        rifiutoMartedi = binding.spinDay2
        rifiutoMercoledi = binding.spinDay3
        rifiutoGiovedi = binding.spinDay4
        rifiutoVenerdi = binding.spinDay5
        rifiutoSabato = binding.spinDay6
        rifiutoDomenica = binding.spinDay7
        btnPopola = binding.btnPopInvio


        btnPopola.setOnClickListener( View.OnClickListener {

            val sharedPref = activity?.getSharedPreferences("sharedPrefsCalendar", Context.MODE_PRIVATE)
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

            dialogBuilder = AlertDialog.Builder(context)
            val contactPopupView = layoutInflater.inflate(R.layout.fragment_popup, null)

            dialogBuilder!!.setView(contactPopupView)
            dialog?.dismiss()
        })


        return binding.root
    }
}

