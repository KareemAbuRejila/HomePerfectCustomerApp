package com.codeshot.home_perfect.models

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

class Service {
    var id:String?=""
    var name:String?=""
    var image:String?=""
    val providers:List<String>?=ArrayList()

    constructor()

    companion object {
        @JvmStatic
        @BindingAdapter("imageUrl")
        fun loadImageProvider(view: ImageView, url: String) {
            Picasso.get().load(url).into(view)
        }
    }

}