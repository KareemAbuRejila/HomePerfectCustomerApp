package com.codeshot.home_perfect.ui.aboutus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.codeshot.home_perfect.databinding.FragmentAboutusBinding

class AboutUsFragment : Fragment() {

    private lateinit var fragmentAboutUsBinding: FragmentAboutusBinding
    private lateinit var shareViewModel: AboutUSViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        shareViewModel = ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
            .create(AboutUSViewModel::class.java)
        fragmentAboutUsBinding = FragmentAboutusBinding.inflate(inflater, container, false)
        shareViewModel.text.observe(viewLifecycleOwner, Observer {
            fragmentAboutUsBinding.textShare.text = it
        })
        return fragmentAboutUsBinding.root
    }
}