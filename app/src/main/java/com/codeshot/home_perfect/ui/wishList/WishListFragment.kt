package com.codeshot.home_perfect.ui.wishList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cc.cloudist.acplibrary.ACProgressBaseDialog
import com.codeshot.home_perfect.adapters.ProvidersAdapter
import com.codeshot.home_perfect.common.Common
import com.codeshot.home_perfect.databinding.FragmentWishListBinding
import com.codeshot.home_perfect.interfaces.ItemProviderListener
import com.codeshot.home_perfect.ui.provider_profile.ProviderProfileDialog


class WishListFragment : Fragment(),
    ItemProviderListener {

    private lateinit var fragmentWishListBinding: FragmentWishListBinding
    private var wishListViewModel: WishListViewModel? = null
    private lateinit var loadingDialog: ACProgressBaseDialog
    private lateinit var providersAdapter: ProvidersAdapter
    private var empty = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (wishListViewModel == null) {
            wishListViewModel =
                ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
                    .create(WishListViewModel::class.java)
        }
        wishListViewModel!!.getInstance(requireContext())
        loadingDialog = Common.LOADING_DIALOG(requireContext())
        loadingDialog.show()
        wishListViewModel!!.getProviders()

        providersAdapter = ProvidersAdapter()
        providersAdapter.setViewType(providersAdapter.PROVIDER_WISHLIST)
        providersAdapter.setOnCLickListener(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentWishListBinding = FragmentWishListBinding.inflate(inflater, container, false)
        return fragmentWishListBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentWishListBinding.adapter = providersAdapter

        fragmentWishListBinding.rvProviders.visibility = View.GONE
        fragmentWishListBinding.imgEmptyWishList.visibility = View.VISIBLE

        wishListViewModel!!.providers.observe(viewLifecycleOwner, Observer {
            providersAdapter.setProviders(it)
            fragmentWishListBinding.rvProviders.visibility = View.VISIBLE
            fragmentWishListBinding.imgEmptyWishList.visibility = View.GONE
            loadingDialog.dismiss()
        })


    }

    override fun onStart() {
        super.onStart()
        loadingDialog.dismiss()
    }

    override fun onProviderClicked(providerId: String) {
        val profileDialog = ProviderProfileDialog(providerId)
        profileDialog.show(
            requireActivity().supportFragmentManager,
            "ProviderProfileDialog"
        )
    }
}