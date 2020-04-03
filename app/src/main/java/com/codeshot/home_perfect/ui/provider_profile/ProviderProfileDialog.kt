package com.codeshot.home_perfect.ui.provider_profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import com.codeshot.home_perfect.common.Common
import com.codeshot.home_perfect.common.Common.CURRENT_TOKEN
import com.codeshot.home_perfect.common.Common.CURRENT_USER_KEY
import com.codeshot.home_perfect.common.Common.CURRENT_USER_NAME
import com.codeshot.home_perfect.common.Common.CURRENT_USER_PHONE
import com.codeshot.home_perfect.common.Common.PROVIDERS_REF
import com.codeshot.home_perfect.common.Common.TOKENS_REF
import com.codeshot.home_perfect.common.Common.USERS_REF
import com.codeshot.home_perfect.R
import com.codeshot.home_perfect.databinding.DialogProviderProfileBinding
import com.codeshot.home_perfect.models.*
import com.codeshot.home_perfect.remote.IFCMService
import com.codeshot.home_perfect.ui.booking_provider.BookingProviderDialog
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProviderProfileDialog : SuperBottomSheetFragment {
    lateinit var dialogProviderProfileBinding: DialogProviderProfileBinding
    private val TAG = "ProviderProfileDialog"

    private lateinit var fcmService: IFCMService
    private var provider: Provider?=null
    constructor() : super()
    constructor(provider: Provider?) : super() {
        this.provider = provider
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Set Dialog To FullScreenTheme
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme)
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
        return dialogProviderProfileBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
                dialogProviderProfileBinding.provider = provider
                dialogProviderProfileBinding.contentProfile.provider=provider
                checkProviderStatus()


//        dialogProviderProfileBinding.fullDialogClose.setOnClickListener { super.dismiss() }
        dialogProviderProfileBinding.btnBookingDialog.setOnClickListener {
//            bookProvider(provider!!.id!!)
            val bookingDialog=BookingProviderDialog(provider)
            bookingDialog.show(childFragmentManager,"BookingDialog")
            }

    }

    private fun checkProviderStatus() {
            PROVIDERS_REF.document(provider!!.id!!).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException!=null)
                return@addSnapshotListener
            else{
                val provider=documentSnapshot!!.toObject(Provider::class.java)
                if (provider!!.online){
                    dialogProviderProfileBinding.btnBookingDialog.text=requireContext().resources.getString(R.string.book)
                    dialogProviderProfileBinding.btnBookingDialog.visibility=View.VISIBLE
//                    dialogProviderProfileBinding.btnBookingDialog.setTextColor(requireActivity().resources.getColor(android.R.color.holo_green_light))
                    dialogProviderProfileBinding.btnBookingDialog.isEnabled=true
                    dialogProviderProfileBinding.contentProfile.status.background=requireContext().resources.getDrawable(R.drawable.ic_status_on)
                }else{
                    dialogProviderProfileBinding.btnBookingDialog.visibility=View.GONE
                    dialogProviderProfileBinding.contentProfile.status.background=requireContext().resources.getDrawable(R.drawable.ic_status_off)

                }


            }
        }

    }

    override fun getPeekHeight(): Int {
        return super.getPeekHeight()+100
    }


    private fun bookProvider(providerID: String) {
        TOKENS_REF.orderBy(FieldPath.documentId())
            .whereEqualTo(FieldPath.documentId(), providerID)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e(TAG, firebaseFirestoreException.message!!)
                    return@addSnapshotListener
                } else {
                    querySnapshot!!.forEach { document ->
                        if (document.id == providerID) {
                            val providerToken = document.toObject(Token::class.java)
                            val userToken = CURRENT_TOKEN
                            val msgContent: MutableMap<String, String> = HashMap()
                            msgContent["title"]="Booking"
                            msgContent["userToken"] = userToken
                            msgContent["userId"]= CURRENT_USER_PHONE
                            msgContent["userKey"]= CURRENT_USER_KEY
                            msgContent["userName"]= CURRENT_USER_NAME
                            val request=Request(CURRENT_USER_PHONE,providerID,"waiting")
                            Common.REQUESTS_REF.add(request)
                                .addOnSuccessListener {
                                msgContent["requestId"]=it.id
                                val dataMessage = DataMessage(providerToken.token, msgContent)
                                val providerTask=PROVIDERS_REF.document(providerID)
                                    .update("requests",FieldValue.arrayUnion(it.id))
                                val userTask= USERS_REF.document(CURRENT_USER_PHONE)
                                    .update("requests",FieldValue.arrayUnion(it.id))
                                Tasks.whenAllSuccess<Toast>(providerTask,userTask)
                                    .addOnSuccessListener {
                                        fcmService.sendMessage(dataMessage)
                                            .enqueue(object : Callback<FCMResponse> {
                                                override fun onResponse(call: Call<FCMResponse>,response: Response<FCMResponse>) {
                                                    if (response.isSuccessful) {
                                                        Toast.makeText(context, "Success", Toast.LENGTH_LONG)
                                                            .show()
                                                    } else {
                                                        Toast.makeText(
                                                            context,
                                                            "Request Failed sent!",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }

                                                override fun onFailure(call: Call<FCMResponse>,t: Throwable) {
                                                    Log.e("ERROR REQUEST SENT ", t.message!!)
                                                    Toast.makeText(
                                                        context,
                                                        "ERROR REQUEST SENT! " + t.message,
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            })
                                    }
                            }
                        }
                    }
                }
            }

    }

}