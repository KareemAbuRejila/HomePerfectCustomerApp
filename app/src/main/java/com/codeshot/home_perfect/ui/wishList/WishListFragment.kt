package com.codeshot.home_perfect.ui.wishList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cc.cloudist.acplibrary.ACProgressBaseDialog
import com.codeshot.home_perfect.adapters.MyBookingAdapter
import com.codeshot.home_perfect.adapters.ProvidersAdapters
import com.codeshot.home_perfect.common.Common
import com.codeshot.home_perfect.common.Common.PROVIDERS_REF
import com.codeshot.home_perfect.databinding.FragmentWishListBinding
import com.codeshot.home_perfect.models.Provider
import com.codeshot.home_perfect.models.User
import com.codeshot.home_perfect.ui.provider_profile.ProviderProfileDialog
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.content_profile_provider.*

class WishListFragment : Fragment(), ProvidersAdapters.OnItemClickListener {

    private lateinit var fragmentWishListBinding: FragmentWishListBinding
    private var wishListViewModel: WishListViewModel? = null
    private lateinit var loadingDialog: ACProgressBaseDialog
    private lateinit var providersAdapters: ProvidersAdapters
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

        val options = FirestoreRecyclerOptions.Builder<Provider>()
            .setQuery(PROVIDERS_REF, Provider::class.java)
            .setLifecycleOwner(viewLifecycleOwner).build()
        providersAdapters = ProvidersAdapters(options = options)
        providersAdapters.setViewType(providersAdapters.WISHLIST_TYPE)
        providersAdapters.setOnCLickListener(this)


        fragmentWishListBinding.adapter = providersAdapters
        wishListViewModel!!.providersOptions.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                fragmentWishListBinding.rvProviders.visibility = View.GONE
                fragmentWishListBinding.imgEmptyWishList.visibility = View.VISIBLE
                return@Observer
            }
            providersAdapters.updateOptions(it)
            fragmentWishListBinding.rvProviders.visibility = View.VISIBLE
            fragmentWishListBinding.imgEmptyWishList.visibility = View.GONE
            loadingDialog.dismiss()
        })


    }

    override fun onStart() {
        super.onStart()
        providersAdapters.startListening()
        loadingDialog.dismiss()
    }

    override fun onItemClicked(providerId: String) {
        Common.PROVIDERS_REF.document(providerId).get()
            .addOnSuccessListener {
                val provider = it.toObject(Provider::class.java)
                provider!!.id = it.id
                val profileDialog = ProviderProfileDialog(provider)
                profileDialog.show(
                    requireActivity().supportFragmentManager,
                    "ProviderProfileDialog"
                )
            }
    }
}