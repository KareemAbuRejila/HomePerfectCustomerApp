package com.codeshot.home_perfect.ui.mybooking

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.SharedElementCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.codeshot.home_perfect.Common.Common
import com.codeshot.home_perfect.R
import com.codeshot.home_perfect.adapters.MyBookingAdapter
import com.codeshot.home_perfect.databinding.FragmentMyBookingBinding
import com.codeshot.home_perfect.models.Provider
import com.codeshot.home_perfect.models.Request
import com.codeshot.home_perfect.ui.home.HomeViewModel
import com.codeshot.home_perfect.ui.provider_profile.ProviderProfileDialog

class MyBookingFragment : Fragment(),
    MyBookingAdapter.ItemRequestListener {

    private lateinit var fragmentMyBookingBinding: FragmentMyBookingBinding
    private lateinit var myBookingViewModel: MyBookingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myBookingViewModel = ViewModelProvider.NewInstanceFactory().create(MyBookingViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMyBookingBinding = FragmentMyBookingBinding.inflate(inflater, container, false)
        return fragmentMyBookingBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val bookingsAdapter = MyBookingAdapter()
        myBookingViewModel.bookings.observe(viewLifecycleOwner, Observer {
            val acProgressBaseDialog = ACProgressFlower.Builder(requireContext())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please Wait ....!")
                .fadeColor(Color.DKGRAY).build()
            acProgressBaseDialog.show()
            bookingsAdapter.setList(it)
            acProgressBaseDialog.dismiss()
        })
        fragmentMyBookingBinding.requestsAdapter = bookingsAdapter
        bookingsAdapter.setItemRequestListener(this)

    }

    override fun OnItemClicked(request: Request) {
        val requestId=request.id
        Toast.makeText(requireContext(),"Request: $requestId",Toast.LENGTH_SHORT).show()
    }

    override fun OnImageClicked(providerId: String) {
        Common.PROVIDERS_REF.document(providerId).get()
            .addOnSuccessListener {
                val provider=it.toObject(Provider::class.java)
                provider!!.id=it.id
                val profileDialog =ProviderProfileDialog(provider)
                profileDialog.show(requireActivity().supportFragmentManager, "ProviderProfileDialog")
            }
    }


}