package com.eco.app

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
    private lateinit var prova : TextView


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
        prova = binding.txtPopD7


        btnPopola.setOnClickListener( View.OnClickListener {
            Toast.makeText(context, "You clicked me.", Toast.LENGTH_SHORT).show()
        })

        return binding.root
    }
}

