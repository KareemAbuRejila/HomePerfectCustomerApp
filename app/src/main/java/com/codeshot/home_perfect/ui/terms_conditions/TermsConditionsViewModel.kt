package com.codeshot.home_perfect.ui.terms_conditions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TermsConditionsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Terns and Conditions Fragment"
    }
    val text: LiveData<String> = _text
}