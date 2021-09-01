package com.codeshot.home_perfect.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.codeshot.home_perfect.R
import com.codeshot.home_perfect.adapters.ProvidersAdapter
import com.codeshot.home_perfect.common.Common
import com.codeshot.home_perfect.databinding.DialogNotificationsBinding
import com.codeshot.home_perfect.models.Notification
import com.codeshot.home_perfect.models.User
import kotlin.streams.toList

class NotificationsDialog : DialogFragment() {
    private lateinit var dialogNotificationsBinding: DialogNotificationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_BookingDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialogNotificationsBinding = DialogNotificationsBinding
            .inflate(inflater, container, false)
        dialogNotificationsBinding.btnNotifBack
            .setOnClickListener { dismiss() }
        return dialogNotificationsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ProvidersAdapter()
        adapter.setViewType(adapter.NOTIFICATION)
        dialogNotificationsBinding.adapter = adapter
        Common.USERS_REF.document(Common.CURRENT_USER_KEY)
            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) return@addSnapshotListener
                val user = documentSnapshot!!.toObject(User::class.java)!!
                user.notifications.stream().map { noti -> noti.msgContent }.toList()
                adapter.setNotifications(user.notifications)
            }


    }
}