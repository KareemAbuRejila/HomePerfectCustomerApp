package com.codeshot.home_perfect.models

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.codeshot.home_perfect.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Request : Serializable {
    var id: String? = null
    var from: String? = null
    var to: String? = null
    var time: Date? = null
    var status: String? = null
    val address = HashMap<String, String>()

    //    var requestLocation: Location?=null
    var additions = ArrayList<Addition>()
    var perHour: Double? = 0.0
    var totalPrice: Double? = 0.0
    var providerUserName: String? = null
    var providerUserImage: String? = null
    var customerUserName: String? = null
    var customerUserImage: String? = null


    constructor(from: String?, to: String?, status: String?) {
        this.from = from
        this.to = to
        this.status = status
    }

    constructor()

    companion object {
        @JvmStatic
        @BindingAdapter("ImgCRequest")
        fun loadCRequestImage(view: CircleImageView?, url: String?) {
            if (url != null)
                Picasso.get().load(url).placeholder(R.drawable.ic_person_black_24dp)
                    .error(android.R.color.holo_red_dark).into(view)
        }

        @SuppressLint("ResourceAsColor")
        @JvmStatic
        @BindingAdapter("statusRequest")
        fun loadRequestStatus(textView: TextView?, status: String?) {
            when (status) {
                "waiting" -> {
                    textView!!.setTextColor(textView.context.resources.getColor(android.R.color.holo_blue_dark))
                    textView.text = textView.context.resources.getString(R.string.waiting)

                }
                "accepted" -> {
                    textView!!.setTextColor(textView.context.resources.getColor(android.R.color.holo_purple))
                    textView.text = textView.context.resources.getString(R.string.done)

                }
                "in progress" -> {
                    textView!!.setTextColor(textView.context.resources.getColor(android.R.color.holo_orange_light))
                    textView.text = textView.context.resources.getString(R.string.in_progress)

                }
                "done" -> {
                    textView!!.setTextColor(textView.context.resources.getColor(android.R.color.holo_green_light))
                    textView.text = textView.context.resources.getString(R.string.accepted)
                }
                "canceled" -> {
                    textView!!.setTextColor(textView.context.resources.getColor(android.R.color.holo_red_light))
                    textView.text = textView.context.resources.getString(R.string.canceled)

                }
                else -> return

            }
        }
    }
}