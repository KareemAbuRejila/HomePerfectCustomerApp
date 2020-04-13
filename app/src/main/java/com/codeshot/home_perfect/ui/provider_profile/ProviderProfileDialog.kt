package com.codeshot.home_perfect.ui.provider_profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import cc.cloudist.acplibrary.ACProgressBaseDialog
import com.codeshot.home_perfect.R
import com.codeshot.home_perfect.common.Common
import com.codeshot.home_perfect.common.Common.CURRENT_USER_KEY
import com.codeshot.home_perfect.common.Common.LOADING_DIALOG
import com.codeshot.home_perfect.common.Common.PROVIDERS_REF
import com.codeshot.home_perfect.common.Common.SHARED_PREF
import com.codeshot.home_perfect.common.Common.USERS_REF
import com.codeshot.home_perfect.databinding.DialogProviderProfileBinding
import com.codeshot.home_perfect.init.MyApplication
import com.codeshot.home_perfect.models.*
import com.codeshot.home_perfect.remote.IFCMService
import com.codeshot.home_perfect.ui.booking_provider.BookingProviderDialog
import com.codeshot.home_perfect.util.UIUtil
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.MetadataChanges
import com.google.gson.Gson


class ProviderProfileDialog : DialogFragment {
    lateinit var dialogProviderProfileBinding: DialogProviderProfileBinding
    private val TAG = "ProviderProfileDialog"

    private lateinit var fcmService: IFCMService
    private var providerId: String? = null
    private var provider: Provider? = Provider()
    private var added = false
    private var user = User()
    private var loadingDialog: ACProgressBaseDialog? = null

    constructor() : super()
    constructor(providerId: String?) : super() {
        this.providerId = providerId
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Set Dialog To FullScreenTheme
        setStyle(STYLE_NORMAL, R.style.AppTheme_ProfileProviderDialogTheme)
        fcmService = Common.FCM_SERVICE
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        dialogProviderProfileBinding =
            DialogProviderProfileBinding.inflate(inflater, container, false)
        loadingDialog = LOADING_DIALOG(requireContext())
        loadingDialog!!.show()
        return dialogProviderProfileBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PROVIDERS_REF.document(providerId!!)
            .addSnapshotListener(MetadataChanges.INCLUDE)
            { documentSnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) return@addSnapshotListener
                provider = documentSnapshot!!.toObject(Provider::class.java)
                provider!!.id = documentSnapshot.id
                if (!documentSnapshot.metadata.isFromCache) {
                    checkProviderStatus()
                } else {
                    provider!!.online = false
                    checkProviderStatus()
                }
                dialogProviderProfileBinding.provider = provider
                dialogProviderProfileBinding.contentProfile.provider = provider
                checkWishList()
            }
        dialogProviderProfileBinding.btnBack.setOnClickListener { dismiss() }
        dialogProviderProfileBinding.btnAddWishList.setOnClickListener {
            if (!added) addToWishList()
            else removeFromWishList()
        }
        dialogProviderProfileBinding.btnBookingDialog.setOnClickListener {
            val bookingDialog = BookingProviderDialog(provider)
            bookingDialog.show(childFragmentManager, "BookingDialog")
        }
        loadingDialog!!.dismiss()

    }

    override fun onStart() {
        super.onStart()
        plusOneToProviderViews()
    }

    private fun checkWishList() {
        val userGSON = MyApplication.getSharedPrf().getString("user", null)
        if (userGSON != null) {
            user = Gson().fromJson<User>(userGSON, User::class.java)
            if (user.wishList.contains(provider!!.id)) {
                added = true
                dialogProviderProfileBinding.btnAddWishList.setImageResource(R.drawable.ic_favorite_black_24dp)
            } else {
                added = false
                dialogProviderProfileBinding.btnAddWishList.setImageResource(R.drawable.ic_favorite_border_24dp)
            }
        }
    }

    private fun addToWishList() {
        val addToWishListTask = USERS_REF.document(CURRENT_USER_KEY)
            .update("wishList", FieldValue.arrayUnion(provider!!.id))
        val plusLikeToProvider = PROVIDERS_REF.document(provider!!.id!!)
            .update("likes", FieldValue.increment(1))

        Tasks.whenAllSuccess<Tasks>(addToWishListTask, plusLikeToProvider)
            .addOnSuccessListener {
                UIUtil.showShortToast("Added to your wishList", requireContext())
                added = true
                user.wishList.add(provider!!.id!!)
                val userGSON = Gson().toJson(user)
                SHARED_PREF(requireContext()).edit().putString("user", userGSON).apply()
                dialogProviderProfileBinding.btnAddWishList.setImageResource(R.drawable.ic_favorite_black_24dp)
            }.addOnFailureListener {
                UIUtil.showShortToast("Check your Internet", requireContext())
            }
    }

    private fun plusOneToProviderViews() {
        PROVIDERS_REF.document(providerId!!)
            .update("views", FieldValue.increment(1))
    }

    private fun removeFromWishList() {
        val removeFromWishListTask = USERS_REF.document(CURRENT_USER_KEY)
            .update("wishList", FieldValue.arrayRemove(provider!!.id))
        val minLikeFromProvider = PROVIDERS_REF.document(provider!!.id!!)
            .update("likes", FieldValue.increment(-1))

        Tasks.whenAllSuccess<Tasks>(removeFromWishListTask, minLikeFromProvider)
            .addOnSuccessListener {
                UIUtil.showLongToast("Removed from your wishList", requireContext())

                added = false
                user.wishList.remove(provider!!.id!!)
                val userGSON = Gson().toJson(user)
                SHARED_PREF(requireContext()).edit().putString("user", userGSON).apply()
                dialogProviderProfileBinding.btnAddWishList.setImageResource(R.drawable.ic_favorite_border_24dp)

            }
            .addOnFailureListener {
                UIUtil.showShortToast("Check your Internet", requireContext())
            }
    }


    private fun checkProviderStatus() {
                    if (provider!!.online) {
                        dialogProviderProfileBinding.btnBookingDialog.visibility = View.VISIBLE
                        dialogProviderProfileBinding.btnBookingDialog.isEnabled = true

                    } else {
                        dialogProviderProfileBinding.btnBookingDialog.visibility = View.GONE
                    }


    }


}