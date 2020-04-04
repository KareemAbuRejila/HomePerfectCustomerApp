package com.codeshot.home_perfect.models

import android.graphics.drawable.Drawable
import android.location.Location
import android.media.Image
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.codeshot.home_perfect.R
import com.firebase.geofire.GeoFire
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.imperiumlabs.geofirestore.GeoFirestore
import java.io.Serializable


class User : Serializable {
    var personalImageUri: String? = null
    var userName: String? = null
    var phone:String?=null
    var id: String? = null
    var requests:List<String>?=ArrayList()
    var email:String?=null
    var bod:String?=null
    var gender:String?=null
    var address=HashMap<String,String>()
    var rate: Double? = 0.0
    var rates = ArrayList<Rate>()
    var wishList = ArrayList<String>()


    constructor(personalImageUri: String, userName: String) {
        this.personalImageUri = personalImageUri
        this.userName = userName
    }

    constructor()

    companion object {
        @JvmStatic
        @BindingAdapter("imageUrl")
        fun loadImage(view: CircleImageView, url: String) {
            Picasso.get().load(url).into(view)
        }
        @JvmStatic
        @BindingAdapter("ImgCUser")
        fun loadCImage(view: CircleImageView?, url: String?) {
            if (url != null)
                Picasso.get().load(url).placeholder(R.drawable.ic_person_black_24dp).into(view)
        }
    }


}