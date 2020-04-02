package com.codeshot.home_perfect.ui.wishList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WishListViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is WishList Fragment"
    }
    val text: LiveData<String> = _text
}