package com.codeshot.home_perfect.ui.booking_provider.steps_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.codeshot.home_perfect.adapters.AdditionsAdapter
import com.codeshot.home_perfect.databinding.FragmentStepSecondBookingBinding
import com.codeshot.home_perfect.models.Addition
import com.codeshot.home_perfect.ui.booking_provider.BookingProviderDialog

/**
 * A simple [Fragment] subclass.
 */
class SecondStepBookingFragment() : Fragment(),
        AdditionsAdapter.OnAdditionClicked{

    lateinit var fragmentStepSecondBookingBinding:FragmentStepSecondBookingBinding
    val additions=ArrayList<Addition>()
    var providerPerHour:Double?=0.0
    var totalPrice:Double?=0.0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentStepSecondBookingBinding=FragmentStepSecondBookingBinding.inflate(inflater,container,false)
        return fragmentStepSecondBookingBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val parentDialog=(parentFragment as BookingProviderDialog)

        val providerName= parentDialog.provider!!.userName
        val providerImage= parentDialog.provider.personalImageUri
        providerPerHour= parentDialog.provider.perHour
        fragmentStepSecondBookingBinding.providerName=providerName
        fragmentStepSecondBookingBinding.providerImg=providerImage
        fragmentStepSecondBookingBinding.providerPerHour=providerPerHour
        totalPrice=providerPerHour!!

        val additions=parentDialog.provider.additions
        val additionsAdapter=AdditionsAdapter()
        fragmentStepSecondBookingBinding.additionsAdapter=additionsAdapter
//        fragmentStepSecondBookingBinding.rvAdditions.adapter=additionsAdapter
        additionsAdapter.setList(additions = additions)
        additionsAdapter.setOnClickedListener(this)
        fragmentStepSecondBookingBinding.totalPrice=totalPrice


    }
    override fun onItemClicked(addition: Addition,position:Int,type:String) {
        if (type=="add"){
            additions.add(addition)
            totalPrice = totalPrice!!.plus(addition.price!!)
        }else{
            additions.remove(addition)
            totalPrice = totalPrice!!.minus(addition.price!!)
        }
        fragmentStepSecondBookingBinding.totalPrice=totalPrice
//        Toast.makeText(activity,additions.size.toString()+"\n"+totalPrice.toString(),Toast.LENGTH_SHORT).show()
    }
}