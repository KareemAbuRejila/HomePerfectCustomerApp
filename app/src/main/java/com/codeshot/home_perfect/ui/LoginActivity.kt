package com.codeshot.home_perfect.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import cc.cloudist.acplibrary.ACProgressBaseDialog
import com.codeshot.home_perfect.HomeActivity
import com.codeshot.home_perfect.R
import com.codeshot.home_perfect.common.Common.LOADING_DIALOG
import com.codeshot.home_perfect.common.Common.PROVIDERS_REF
import com.codeshot.home_perfect.databinding.ActivityLoginBinding
import com.codeshot.home_perfect.databinding.DialogLoginBinding
import com.codeshot.home_perfect.util.UIUtil
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import java.util.concurrent.TimeUnit


class LoginActivity : AppCompatActivity() {
    private val TAG: String = "LOGIN ACTIVITY"
    private lateinit var activityLoginBinding: ActivityLoginBinding
    private lateinit var loadingDialog: ACProgressBaseDialog
    private var phoneNumber = ""
    private var codeSent: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLoginBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_login
        )
        loadingDialog = LOADING_DIALOG(this)
        activityLoginBinding.ccpLogin.registerPhoneNumberTextView(activityLoginBinding.edtPhoneLogin)

        activityLoginBinding.edtPhoneLogin
            .setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    return@OnEditorActionListener true
                }
                false
            })
        activityLoginBinding.btnConLogin.setOnClickListener { v: View? ->
            sendVerificationCode()
        }
        activityLoginBinding.edtPhoneLogin.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) sendVerificationCode()
            true
        }
        activityLoginBinding.codeInput.addOnCompleteListener { code: String? ->
            verifySignInCode(code)
        }

    }

    private fun sendVerificationCode() {
        phoneNumber = activityLoginBinding.ccpLogin.fullNumberWithPlus
        when {
            phoneNumber.isEmpty() -> {
                activityLoginBinding.edtPhoneLogin.error = "Phone Number is required  "
            }
            phoneNumber.length < 10 -> {
                activityLoginBinding.edtPhoneLogin.error = "Check your Phone Number "
            }
            else -> {
                loadingDialog.show()
                PROVIDERS_REF.whereEqualTo("phone", phoneNumber)
                    .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                        if (firebaseFirestoreException != null) return@addSnapshotListener
                        if (querySnapshot!!.isEmpty) {
                            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                phoneNumber,  // Phone number to verify
                                60,  // Timeout duration
                                TimeUnit.SECONDS,  // Unit of timeout
                                this,  // Activity (for callback binding)
                                mCallbacks
                            ) // OnVerificationStateChangedCallbacks
                        } else {
                            loadingDialog.dismiss()
                            UIUtil.showLongToast("This Number used by Provider Account", this)
                            return@addSnapshotListener
                        }
                    }


            }
        }
    }

    private val mCallbacks: OnVerificationStateChangedCallbacks =
        object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential)

            }

            override fun onVerificationFailed(e: FirebaseException) {
                UIUtil.showLongToast(e.message!!, this@LoginActivity)
                Log.e(TAG, e.message!!)
                loadingDialog.dismiss()
            }

            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                loadingDialog.dismiss()
                showCodeInput()
                codeSent = s
            }
        }

    private fun showCodeInput() {
//        activityLoginBinding.btnConLogin.visibility = View.GONE
//        activityLoginBinding.phoneLayoutLogin.visibility = View.GONE
//        activityLoginBinding.textView3.visibility = View.GONE

        activityLoginBinding.groupDefult.visibility = View.GONE
        activityLoginBinding.phoneNumber = phoneNumber
        activityLoginBinding.groupCode.visibility = View.VISIBLE

//        activityLoginBinding.tvCodeEnter.visibility=View.VISIBLE
//        activityLoginBinding.codeInput.visibility = View.VISIBLE
        back = false

    }

    private fun hideCodeInput() {
        activityLoginBinding.groupDefult.visibility = View.VISIBLE
        activityLoginBinding.phoneNumber = phoneNumber
        activityLoginBinding.groupCode.visibility = View.GONE
    }

    private fun verifySignInCode(code: String?) {
        if (TextUtils.isEmpty(code)) {
            UIUtil.showLongToast("Code IS Empty", this@LoginActivity)

        } else {
            loadingDialog.show()
            val credential = PhoneAuthProvider.getCredential(codeSent, code!!)
            signInWithPhoneAuthCredential(credential)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    UIUtil.showShortToast("Is logged", this@LoginActivity)
                    loadingDialog.dismiss()
                    sendToHomeActivity("new")
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        val errorMsg = task.exception.toString()
                        loadingDialog.dismiss()
                        UIUtil.showLongToast(
                            "Incorrect Verification Code $errorMsg",
                            this@LoginActivity
                        )

                        Log.e(TAG, errorMsg)
                    }
                }
            }
    }

    fun showLoginDialog(view: View) {
        val alertDialog =
            AlertDialog.Builder(this)
                .setPositiveButton(
                    "Login"
                ) { dialog, which ->
                    loginDialog()
                }
                .setNegativeButton("SignUp") { dialog, which ->
                    signUpDialog()
                }.create()
        alertDialog.show()
    }

    private fun signUpDialog() {
        val dialogbinding = DialogLoginBinding.inflate(
            layoutInflater,
            activityLoginBinding.root as ViewGroup?, false
        )
        dialogbinding.btnLogin.text = "SignUp"
        dialogbinding.btnLogin.setOnClickListener {
            val email = dialogbinding.edtEmailLogDia.text.toString()
            val pass = dialogbinding.edtPassLogDia.text.toString()
            val repass = dialogbinding.edtRePassLogDia.text.toString()
            if (pass == repass) {
                loadingDialog.show()
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
                    .addOnSuccessListener {
                        sendToHomeActivity("new")
                        loadingDialog.dismiss()
                    }
            }
        }

        val signupDiaog = AlertDialog.Builder(this)
            .setView(dialogbinding.root).create()
        signupDiaog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        signupDiaog.show()
    }

    private fun loginDialog() {
        val dialogbinding = DialogLoginBinding.inflate(
            layoutInflater,
            activityLoginBinding.root as ViewGroup?, false
        )
        dialogbinding.rePassLogDia.visibility = View.INVISIBLE
        dialogbinding.btnLogin.text = "Login"
        dialogbinding.btnLogin.setOnClickListener {
            loadingDialog.show()
            val email = dialogbinding.edtEmailLogDia.text.toString()
            val pass = dialogbinding.edtPassLogDia.text.toString()
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener {
                    loadingDialog.dismiss()
                    sendToHomeActivity("new")
                }.addOnFailureListener {
                    loadingDialog.dismiss()
                    UIUtil.showLongToast(it.message!!, this@LoginActivity)

                }
        }

        val loginDialog = AlertDialog.Builder(this, R.style.AppTheme_ServiceActivity)
            .setView(dialogbinding.root).create()
        loginDialog.show()
        loginDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


    }

    private fun sendToHomeActivity(type: String) {
        val homeIntent = Intent(this, HomeActivity::class.java)
        homeIntent.putExtra("user", type)
        startActivity(homeIntent)
        finish()
    }

    private var back = true
    override fun onBackPressed() {
        if (!back) {
            hideCodeInput()
        } else
            super.onBackPressed()
    }
}
