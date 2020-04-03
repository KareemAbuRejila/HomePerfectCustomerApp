package com.codeshot.home_perfect.ui.wishList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.codeshot.home_perfect.databinding.FragmentWishListBinding

class WishListFragment : Fragment() {

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
        slideshowViewModel.text.observe(viewLifecycleOwner, Observer {
            fragmentWishListBinding.textSlideshow.text = it
        })
    }
}