package com.codeshot.home_perfect.ui.aboutus

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AboutUSViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is About US Fragment"
    }
    val text: LiveData<String> = _text
}