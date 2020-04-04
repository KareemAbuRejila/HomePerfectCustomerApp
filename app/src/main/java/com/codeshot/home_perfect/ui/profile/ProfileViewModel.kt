package com.codeshot.home_perfect.ui.profile

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codeshot.home_perfect.common.Common.CURRENT_USER_KEY
import com.codeshot.home_perfect.common.Common.SHARED_PREF
import com.codeshot.home_perfect.common.Common.USERS_REF
import com.codeshot.home_perfect.models.User
import com.google.gson.Gson

class ProfileViewModel : ViewModel() {

    private var sharedPreferences: SharedPreferences? = null
    val user = MutableLiveData<User>()

    fun getInstance(context: Context) {
        if (sharedPreferences != null) return
        sharedPreferences = SHARED_PREF(context = context)
    }

    fun getUser() {
        USERS_REF.document(CURRENT_USER_KEY)
            .get().addOnSuccessListener {
                user.value = it.toObject(User::class.java)
            }.addOnSuccessListener {
                val userGSON = sharedPreferences!!.getString("user", null)
                if (userGSON != null) {
                    val user = Gson().fromJson<User>(userGSON, User::class.java)
                    this.user.value = user
                }
            }
    }

}