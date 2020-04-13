package com.codeshot.home_perfect.ui.aboutus

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codeshot.home_perfect.common.Common.ROOT_REF
import java.util.*

class AboutUSViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        val currentlng = Locale.getDefault().displayLanguage
        var lng: String? = "en"
        when (currentlng) {
            "English" -> lng = "en"
            "العربية" -> lng = "ar"
        }
        ROOT_REF.collection("AboutUs")
            .document(lng!!).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val mainAbout = it["mainAbout"].toString()
                    value = mainAbout
                }
            }
    }
    val text: LiveData<String> = _text
}