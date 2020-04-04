package com.codeshot.home_perfect.ui.booking_provider.steps_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codeshot.home_perfect.R
import com.codeshot.home_perfect.adapters.AdditionsAdapter
import com.codeshot.home_perfect.databinding.FragmentStepThirdBookingBinding
import com.codeshot.home_perfect.models.Addition
import com.codeshot.home_perfect.ui.booking_provider.BookingProviderDialog

/**
 * A simple [Fragment] subclass.
 */
class ThirdStepBookingFragment : Fragment() {

    private lateinit var fragmentStepThirdBookingBinding: FragmentStepThirdBookingBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentStepThirdBookingBinding =
            FragmentStepThirdBookingBinding.inflate(inflater, container, false)

        return fragmentStepThirdBookingBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parentDialog = (parentFragment as BookingProviderDialog)

        val request = parentDialog.request
        fragmentStepThirdBookingBinding.request=request
        setUpAdditionsList(request.additions)

    }

    private fun setUpAdditionsList(additions: ArrayList<Addition>) {
        if (additions.size != 0) {
            val adapter= AdditionsAdapter()
            adapter.setList(additions)
            fragmentStepThirdBookingBinding.additionsAdapter=adapter

        }
    }
}