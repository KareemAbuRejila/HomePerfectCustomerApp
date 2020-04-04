package com.codeshot.home_perfect.ui.mybooking

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cc.cloudist.acplibrary.ACProgressBaseDialog
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.codeshot.home_perfect.common.Common
import com.codeshot.home_perfect.adapters.MyBookingAdapter
import com.codeshot.home_perfect.databinding.FragmentMyBookingBinding
import com.codeshot.home_perfect.models.Provider
import com.codeshot.home_perfect.models.Request
import com.codeshot.home_perfect.ui.provider_profile.ProviderProfileDialog

class MyBookingFragment : Fragment(),
    MyBookingAdapter.ItemRequestListener {

    private lateinit var fragmentMyBookingBinding: FragmentMyBookingBinding
    private lateinit var myBookingViewModel: MyBookingViewModel
    private lateinit var loadingDialog: ACProgressBaseDialog
    private lateinit var bookingAdapter: MyBookingAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myBookingViewModel =
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
                .create(MyBookingViewModel::class.java)
        loadingDialog = Common.LOADING_DIALOG(requireContext())
        loadingDialog.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMyBookingBinding = FragmentMyBookingBinding.inflate(inflater, container, false)
        return fragmentMyBookingBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookingAdapter = MyBookingAdapter()
        myBookingViewModel.bookings.observe(viewLifecycleOwner, Observer {
            bookingAdapter.setList(it)
        })
        fragmentMyBookingBinding.requestsAdapter = bookingAdapter
        bookingAdapter.setItemRequestListener(this)

    }

    override fun onStart() {
        super.onStart()
        loadingDialog.dismiss()

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