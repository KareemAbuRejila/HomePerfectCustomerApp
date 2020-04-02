package com.codeshot.home_perfect.ui.booking_provider.steps_fragments

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codeshot.home_perfect.databinding.FragmentStepFirstBookingBinding
import com.codeshot.home_perfect.interfaces.OnPassData
import com.codeshot.home_perfect.ui.booking_provider.BookingProviderDialog
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class FirstStepBookingFragment : Fragment() {
    lateinit var fragmentStepFirstBookingBinding: FragmentStepFirstBookingBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentStepFirstBookingBinding=FragmentStepFirstBookingBinding
            .inflate(inflater,container,false)
        return fragmentStepFirstBookingBinding.root
    }

    val calendar=Calendar.getInstance()
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val parentDialog=(parentFragment as BookingProviderDialog)

        fragmentStepFirstBookingBinding.edtCity.onFocusChangeListener=
            View.OnFocusChangeListener { v, hasFocus ->
                parentDialog.dialogBookingProviderBinding
                    .tvNextToolBarBookingActivity
                    .visibility=View.VISIBLE
            }
        val time=calendar.time
        fragmentStepFirstBookingBinding.tvFrom.text=time.toString()
        fragmentStepFirstBookingBinding.tvFrom.setOnClickListener {
            val timePicker= TimePickerDialog(activity,TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY,hourOfDay)
                calendar.set(Calendar.MINUTE,minute)
                fragmentStepFirstBookingBinding.tvFrom.text= calendar.time.toString()
            },Calendar.HOUR_OF_DAY,Calendar.MINUTE,false)
            timePicker.show()

        }

    }
}