package com.codeshot.home_perfect.ui.wishList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cc.cloudist.acplibrary.ACProgressBaseDialog
import com.codeshot.home_perfect.adapters.ProvidersAdapters
import com.codeshot.home_perfect.common.Common
import com.codeshot.home_perfect.databinding.FragmentWishListBinding
import com.codeshot.home_perfect.models.Provider
import com.codeshot.home_perfect.ui.provider_profile.ProviderProfileDialog
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class WishListFragment : Fragment(), ProvidersAdapters.OnItemClickListener {

    private lateinit var fragmentWishListBinding: FragmentWishListBinding
    private lateinit var slideshowViewModel: WishListViewModel
    private lateinit var loadingDialog: ACProgressBaseDialog
    private lateinit var providersAdapters: ProvidersAdapters


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        slideshowViewModel =
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
                .create(WishListViewModel::class.java)
        loadingDialog = Common.LOADING_DIALOG(requireContext())
        loadingDialog.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentWishListBinding=FragmentWishListBinding.inflate(inflater,container,false)
        return fragmentWishListBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val query = Common.PROVIDERS_REF
        val options = FirestoreRecyclerOptions.Builder<Provider>()
            .setQuery(query, Provider::class.java)
            .build()
        providersAdapters = ProvidersAdapters(options)
        providersAdapters.setOnCLickListener(this)
        providersAdapters.setViewType(providersAdapters.WISHLIST_TYPE)
        fragmentWishListBinding.adapter = providersAdapters
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