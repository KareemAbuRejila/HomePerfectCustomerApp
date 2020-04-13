package com.codeshot.home_perfect.ui.aboutus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.codeshot.home_perfect.databinding.FragmentAboutusBinding
import com.codeshot.home_perfect.util.UIUtil
import java.util.*

class AboutUsFragment : Fragment() {

    private lateinit var fragmentAboutUsBinding: FragmentAboutusBinding
    private lateinit var shareViewModel: AboutUSViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shareViewModel = ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
            .create(AboutUSViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentAboutUsBinding = FragmentAboutusBinding.inflate(inflater, container, false)
        return fragmentAboutUsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shareViewModel.text.observe(viewLifecycleOwner, Observer {
            fragmentAboutUsBinding.tvAboutUs.text = it
        })
    }
}