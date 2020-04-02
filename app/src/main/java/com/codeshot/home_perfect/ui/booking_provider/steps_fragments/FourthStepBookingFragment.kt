package com.codeshot.home_perfect.ui.booking_provider.steps_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codeshot.home_perfect.R

/**
 * A simple [Fragment] subclass.
 */
class FourthStepBookingFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.fragment_step_fourth_booking,
            container,
            false
        )
    }
}