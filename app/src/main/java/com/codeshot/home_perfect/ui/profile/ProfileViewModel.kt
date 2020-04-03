package com.codeshot.home_perfect.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codeshot.home_perfect.common.Common.CURRENT_USER_KEY
import com.codeshot.home_perfect.common.Common.USERS_REF
import com.codeshot.home_perfect.models.User

class ProfileViewModel : ViewModel() {

    private val _user = MutableLiveData<User>().apply {
        USERS_REF.document(CURRENT_USER_KEY)
            .get().addOnSuccessListener {
                value=it.toObject(User::class.java)
            }
    }
    val user: LiveData<User> = _user
}