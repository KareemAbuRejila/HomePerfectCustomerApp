package com.codeshot.home_perfect.ui.wishList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.codeshot.home_perfect.adapters.ProvidersAdapters
import com.codeshot.home_perfect.common.Common
import com.codeshot.home_perfect.databinding.FragmentWishListBinding
import com.codeshot.home_perfect.models.Provider
import com.codeshot.home_perfect.ui.provider_profile.ProviderProfileDialog
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class WishListFragment : Fragment(), ProvidersAdapters.OnItemClickListener {

    private lateinit var fragmentWishListBinding: FragmentWishListBinding
    private lateinit var slideshowViewModel: WishListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        slideshowViewModel =
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
                .create(WishListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentWishListBinding=FragmentWishListBinding.inflate(inflater,container,false)
        return fragmentWishListBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val query = Common.PROVIDERS_REF
        val options = FirestoreRecyclerOptions.Builder<Provider>()
            .setQuery(query, Provider::class.java)
            .build()
        val providersAdapter = ProvidersAdapters(options)
        providersAdapter.setOnCLickListener(this)
        providersAdapter.setViewType(providersAdapter.WISHLIST_TYPE)
        fragmentWishListBinding.adapter = providersAdapter

        providersAdapter.startListening()
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