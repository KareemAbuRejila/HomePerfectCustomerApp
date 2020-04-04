package com.codeshot.home_perfect.ui.booking_provider

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.codeshot.home_perfect.common.Common
import com.codeshot.home_perfect.common.Common.CURRENT_USER_IMAGE
import com.codeshot.home_perfect.common.Common.CURRENT_USER_KEY
import com.codeshot.home_perfect.common.Common.CURRENT_USER_NAME
import com.codeshot.home_perfect.common.Common.PROVIDERS_REF
import com.codeshot.home_perfect.common.Common.USERS_REF
import com.codeshot.home_perfect.R
import com.codeshot.home_perfect.common.Common.LOADING_DIALOG
import com.codeshot.home_perfect.databinding.DialogBookingProviderBinding
import com.codeshot.home_perfect.models.*
import com.codeshot.home_perfect.remote.IFCMService
import com.codeshot.home_perfect.ui.booking_provider.steps_fragments.FirstStepBookingFragment
import com.codeshot.home_perfect.ui.booking_provider.steps_fragments.SecondStepBookingFragment
import com.codeshot.home_perfect.ui.booking_provider.steps_fragments.ThirdStepBookingFragment
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.kofigyan.stateprogressbar.StateProgressBar

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.HashMap

class BookingProviderDialog(val provider: Provider?) : DialogFragment() {
    lateinit var dialogBookingProviderBinding: DialogBookingProviderBinding

    private lateinit var fcmService: IFCMService
    val request=Request()

    private val firstStep = FirstStepBookingFragment()
    private val secondStep = SecondStepBookingFragment()
    private val thirdStep = ThirdStepBookingFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BookingDialogTheme)
        super.setCancelable(false)
        fcmService = Common.FCM_SERVICE

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialogBookingProviderBinding=DialogBookingProviderBinding.inflate(inflater,container,false)
        return dialogBookingProviderBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        childFragmentManager.beginTransaction()
            .replace(R.id.contentOfBookingSteps, firstStep).commit()

        dialogBookingProviderBinding.imgbtnCloseToolBarBookingActivity.setOnClickListener { dialog!!.dismiss() }
        dialogBookingProviderBinding.imgbtnBackToolBarBookingActivity.setOnClickListener { backFragment() }
        dialogBookingProviderBinding.tvNextToolBarBookingActivity.setOnClickListener { nextFragment() }
        dialogBookingProviderBinding.tvBookToolBarBookingActivity.setOnClickListener {bookProvider(providerID = provider!!.id!!)  }
    }


    private fun nextFragment() {
        when (dialogBookingProviderBinding.stateProgressBarBooking.currentStateNumber) {
            1 -> {
                dialogBookingProviderBinding.stateProgressBarBooking.setCurrentStateNumber(StateProgressBar.StateNumber.TWO)
                childFragmentManager.beginTransaction()
                    .replace(R.id.contentOfBookingSteps, secondStep).commit()
                dialogBookingProviderBinding.imgbtnCloseToolBarBookingActivity.visibility =
                    View.GONE
                dialogBookingProviderBinding.imgbtnBackToolBarBookingActivity.visibility =
                    View.VISIBLE
                request.customerUserName= CURRENT_USER_NAME
                request.customerUserImage= CURRENT_USER_IMAGE
                request.providerUserName=provider!!.userName
                request.providerUserImage=provider.personalImageUri
                request.address["city"]=firstStep.fragmentStepFirstBookingBinding.edtCity.text.toString()
                request.address["street"]=firstStep.fragmentStepFirstBookingBinding.edtStreet.text.toString()
                request.address["home"]=firstStep.fragmentStepFirstBookingBinding.edtHome.text.toString()
                request.address["level"]=firstStep.fragmentStepFirstBookingBinding.edtFlaor.text.toString()
                request.time=firstStep.calendar.time
            }
            2 -> {
                dialogBookingProviderBinding.stateProgressBarBooking.setCurrentStateNumber(StateProgressBar.StateNumber.THREE)
                childFragmentManager.beginTransaction()
                    .replace(R.id.contentOfBookingSteps, thirdStep).commit()
                dialogBookingProviderBinding.tvNextToolBarBookingActivity.setVisibility(View.GONE)
                dialogBookingProviderBinding.tvBookToolBarBookingActivity.setVisibility(View.VISIBLE)
                request.additions=secondStep.additions
                request.totalPrice=secondStep.totalPrice
                request.perHour=provider!!.perHour
            }
        }
    }
    private fun backFragment() {
        when (dialogBookingProviderBinding.stateProgressBarBooking.currentStateNumber) {
            3 -> {
                dialogBookingProviderBinding.stateProgressBarBooking.setCurrentStateNumber(StateProgressBar.StateNumber.TWO)
                childFragmentManager.beginTransaction()
                    .replace(R.id.contentOfBookingSteps, secondStep).commit()
                dialogBookingProviderBinding.tvNextToolBarBookingActivity.setVisibility(View.VISIBLE)
                dialogBookingProviderBinding.tvBookToolBarBookingActivity.setVisibility(View.GONE)
            }
            2 -> {
                dialogBookingProviderBinding.stateProgressBarBooking.setCurrentStateNumber(StateProgressBar.StateNumber.ONE)
                childFragmentManager.beginTransaction()
                    .replace(R.id.contentOfBookingSteps, firstStep).commit()
                dialogBookingProviderBinding.imgbtnCloseToolBarBookingActivity.setVisibility(View.VISIBLE)
                dialogBookingProviderBinding.imgbtnBackToolBarBookingActivity.setVisibility(View.GONE)
            }
        }
    }

    private fun bookProvider(providerID: String) {
        val loadingDialog = LOADING_DIALOG(requireContext())
        loadingDialog.show()
        Common.TOKENS_REF.orderBy(FieldPath.documentId())
            .whereEqualTo(FieldPath.documentId(), providerID)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e(TAG, firebaseFirestoreException.message!!)
                    return@addSnapshotListener
                } else {
                    querySnapshot!!.forEach { document ->
                        if (document.id == providerID) {
                            val providerToken = document.toObject(Token::class.java)
                            val userToken = Common.CURRENT_TOKEN
                            val msgContent: MutableMap<String, String> = HashMap()
                            msgContent["title"]="Booking"
                            msgContent["userToken"] = userToken
                            msgContent["userPhone"]= Common.CURRENT_USER_PHONE
                            msgContent["userKey"] = CURRENT_USER_KEY
                            msgContent["userName"] = CURRENT_USER_NAME
//                            val request= Request(Common.CURRENT_USER_PHONE,providerID,"waiting")
                            request.from = CURRENT_USER_KEY
                            request.to=providerID
                            request.status="waiting"
//                            request.requestLocation= CURRENT_LOCATION

                            Common.REQUESTS_REF.add(request)
                                .addOnSuccessListener {
                                    msgContent["requestId"]=it.id
                                    val dataMessage = DataMessage(providerToken.token, msgContent)
                                    val providerTask=PROVIDERS_REF.document(providerID)
                                        .update("requests", FieldValue.arrayUnion(it.id))
                                    val userTask= USERS_REF.document(CURRENT_USER_KEY)
                                        .update("requests",FieldValue.arrayUnion(it.id))
                                    Tasks.whenAllSuccess<Toast>(providerTask,userTask)
                                        .addOnSuccessListener {
                                            fcmService.sendMessage(dataMessage)
                                                .enqueue(object : Callback<FCMResponse> {
                                                    override fun onResponse(call: Call<FCMResponse>,response: Response<FCMResponse>) {
                                                        if (response.isSuccessful) {
                                                            Toast.makeText(dialog!!.context, "Success", Toast.LENGTH_LONG)
                                                                .show()
                                                            loadingDialog.dismiss()
                                                            dialog!!.dismiss()
                                                        } else {
                                                            Toast.makeText(
                                                                dialog!!.context,
                                                                "Request Failed sent!",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }
                                                    override fun onFailure(call: Call<FCMResponse>,t: Throwable) {
                                                        Log.e("ERROR REQUEST SENT ", t.message!!)
                                                        Toast.makeText(
                                                            dialog!!.context,
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