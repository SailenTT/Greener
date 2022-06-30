package com.eco.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.eco.app.databinding.FragmentCalculatorBinding
import com.google.android.material.card.MaterialCardView

class CalculatorFragment : Fragment() {

    private lateinit var binding: FragmentCalculatorBinding

    private lateinit var viewPager : ViewPager2
    private lateinit var fragments : ArrayList<Fragment>

    //VAR "Journey Mode"
    private lateinit var cardAuto : MaterialCardView
    private lateinit var cardBus : MaterialCardView
    private lateinit var cardTreno : MaterialCardView
    private lateinit var cardTaxi : MaterialCardView
    private lateinit var cardMoto : MaterialCardView
    private lateinit var cardWalk : MaterialCardView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        //stuff per fare workare bene il viewPager
        binding = FragmentCalculatorBinding.inflate(inflater, container, false)

        viewPager = binding.pagerCFP

        fragments = arrayListOf(
            JourneyModeFragment(),
            JourneyTimeFragment(),
            YourFlightFragment(),
            FoodTrackingFragment()
        )

        val adapter = ViewPagerCFPAdapter(fragments, this)
        viewPager.adapter = adapter

        //BINDING "Journey Mode"
        //cardAuto = viewPager.findViewById(R.id.cvAuto)

        /*//Listener per settare checked le varie card presenti nella GridView
        cardAuto.setOnLongClickListener{
            cardAuto.isChecked = !cardAuto.isChecked
            true
        }*/



        return binding.root
    }

}