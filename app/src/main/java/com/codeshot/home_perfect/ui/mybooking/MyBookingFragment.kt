package com.codeshot.home_perfect.ui.mybooking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cc.cloudist.acplibrary.ACProgressBaseDialog
import com.codeshot.home_perfect.common.Common
import com.codeshot.home_perfect.adapters.ProvidersAdapter
import com.codeshot.home_perfect.databinding.FragmentMyBookingBinding
import com.codeshot.home_perfect.models.Provider
import com.codeshot.home_perfect.models.Request

class MyBookingFragment : Fragment(),
    ProvidersAdapter.ItemRequestListener {

    private lateinit var fragmentMyBookingBinding: FragmentMyBookingBinding
    private lateinit var myBookingViewModel: MyBookingViewModel
    private lateinit var loadingDialog: ACProgressBaseDialog
    private lateinit var bookingAdapter: ProvidersAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myBookingViewModel =
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
                .create(MyBookingViewModel::class.java)
        myBookingViewModel.getInstance(requireContext())
        loadingDialog = Common.LOADING_DIALOG(requireContext())
        loadingDialog.show()
        myBookingViewModel.getRequest()

        bookingAdapter = ProvidersAdapter()
        bookingAdapter.setViewType(bookingAdapter.REQUEST)
        bookingAdapter.setItemRequestListener(this)

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
        fragmentMyBookingBinding.requestsAdapter = bookingAdapter
        fragmentMyBookingBinding.rvRequests.visibility = View.GONE
        fragmentMyBookingBinding.imgEmpty.visibility = View.VISIBLE
        myBookingViewModel.bookings.observe(viewLifecycleOwner, Observer {
            bookingAdapter.setRequestsList(it)
            fragmentMyBookingBinding.rvRequests.visibility = View.VISIBLE
            fragmentMyBookingBinding.imgEmpty.visibility = View.GONE
        })
        loadingDialog.dismiss()

    }

    override fun OnItemClicked(request: Request) {
        val requestId = request.id
//        Toast.makeText(requireContext(), "Request: $requestId", Toast.LENGTH_SHORT).show()
    }
    override fun OnImageClicked(providerId: String) {
        Common.PROVIDERS_REF.document(providerId).get()
            .addOnSuccessListener {
                val provider = it.toObject(Provider::class.java)
                provider!!.id = it.id

            }
    }


}