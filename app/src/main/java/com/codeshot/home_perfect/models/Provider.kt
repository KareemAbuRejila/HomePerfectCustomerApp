package com.codeshot.home_perfect.models

import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.databinding.BindingAdapter
import com.codeshot.home_perfect.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.Serializable


class Provider : Serializable {
    var personalImageUri: String ?=""
    var userName: String? = ""
    var id: String? = null
    var phone: String? = ""
    var birthDay: String? = null
    var nationality: String? = ""
    var gender: String? = ""
    var age: Int? = null
    var serviceType: String? = ""
    var rate: Double? = 0.0
    var perHour: Double? = 0.0
    var address: Map<String, String>? = HashMap()
    var online: Boolean = false
    var requests= ArrayList<String>()
    var additions= ArrayList<Addition>()

    constructor()
    constructor(personalImageUri: String?, userName: String?, serviceType: String?) {
        this.personalImageUri = personalImageUri
        this.userName = userName
        this.serviceType = serviceType
    }

    constructor(personalImageUri: String?, userName: String?, id: String?, rate: Double?) {
        this.personalImageUri = personalImageUri
        this.userName = userName
        this.id = id
        this.rate = rate
    }


    companion object {
        @JvmStatic
        @BindingAdapter("ImgVProvider")
        fun loadImageProvider(view: ImageView, url: String) {
            if (url != null && url != "")
                Picasso.get().load(url).placeholder(R.drawable.ic_person_black_24dp)
                    .error(R.color.error_color).into(view)

        }

        @JvmStatic
        @BindingAdapter("ImgCProvider")
        fun loadImage(view: CircleImageView?, url: String?) {
            if (url != null)
                Picasso.get().load(url).placeholder(R.drawable.ic_person_black_24dp)
                    .error(R.color.error_color).into(view)
        }

        @JvmStatic
        @BindingAdapter("ImgCProviderStatus")
        fun loadImageStatus(view: CircleImageView?, online: Boolean) {
            if (online)
                view!!.setImageResource(R.color.greenLight)
            else
                view!!.setImageResource(R.color.redLight)

        }

        @JvmStatic
        @BindingAdapter("providerStatus")
        fun loadStatus(textView: TextView, online: Boolean) {
            if (online) {
                textView.text = textView.context.resources.getString(R.string.online)
                textView.setBackgroundColor(textView.context.resources.getColor(R.color.greenLight))
            } else {
                textView.text = textView.context.resources.getString(R.string.offline)
                textView.setBackgroundColor(textView.context.resources.getColor(R.color.redLight))

            }
        }

        @JvmStatic
        @BindingAdapter("ratingValue")
        fun setRating(ratingBar: AppCompatRatingBar, mVoteAverage: Double) {
            ratingBar.rating = mVoteAverage.toFloat()
        }
    }
}