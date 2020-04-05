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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import cc.cloudist.acplibrary.ACProgressBaseDialog
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.codeshot.home_perfect.HomeActivity
import com.codeshot.home_perfect.R
import com.codeshot.home_perfect.common.Common.LOADING_DIALOG
import com.codeshot.home_perfect.databinding.ActivityLoginBinding
import com.codeshot.home_perfect.databinding.DialogLoginBinding
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
        isLogined()
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
            verifySignInCode()
        }

    }

    private fun isLogined() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            sendToHomeActivity("old")
        } else
            return
    }

    private fun sendVerificationCode() {
        phoneNumber = activityLoginBinding.ccpLogin.fullNumberWithPlus
        Toast.makeText(
            this@LoginActivity,
            phoneNumber,
            Toast.LENGTH_SHORT
        ).show()
        when {
            phoneNumber.isEmpty() -> {
                activityLoginBinding.edtPhoneLogin.error = "Phone Number is required  "
            }
            phoneNumber.length < 10 -> {
                activityLoginBinding.edtPhoneLogin.error = "Check your Phone Number "
            }
            else -> {
                loadingDialog.show()
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,  // Phone number to verify
                    60,  // Timeout duration
                    TimeUnit.SECONDS,  // Unit of timeout
                    this,  // Activity (for callback binding)
                    mCallbacks
                ) // OnVerificationStateChangedCallbacks
            }
        }
    }

    private val mCallbacks: OnVerificationStateChangedCallbacks =
        object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential)

            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
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
//        activityLoginBinding.codeInput.visibility = View.GONE
//        activityLoginBinding.tvCodeEnter.visibility=View.GONE
//        activityLoginBinding.btnConLogin.visibility = View.VISIBLE
//        activityLoginBinding.phoneLayoutLogin.visibility = View.VISIBLE
//        activityLoginBinding.textView3.visibility = View.VISIBLE

        activityLoginBinding.groupDefult.visibility = View.VISIBLE
        activityLoginBinding.phoneNumber = phoneNumber
        activityLoginBinding.groupCode.visibility = View.GONE
    }

    private fun verifySignInCode() {
        val code: String = activityLoginBinding.codeInput.code.toString()
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(
                this@LoginActivity,
                "Code IS Empty", Toast.LENGTH_LONG
            ).show()
        } else {
            loadingDialog.show()
            val credential = PhoneAuthProvider.getCredential(codeSent, code)
            signInWithPhoneAuthCredential(credential)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
//                    val userID: String = FirebaseAuth.getInstance().currentUser?.uid.toString()
//                    val drviceToken =FirebaseInstanceId.getInstance().token
                    Toast.makeText(
                        this@LoginActivity,
                        " " + "IS Logined",
                        Toast.LENGTH_LONG
                    ).show()
                    loadingDialog.dismiss()
                    sendToHomeActivity("new")
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        val errorMsg = task.exception.toString()
                        loadingDialog.dismiss()
                        Toast.makeText(
                            this@LoginActivity,
                            "Incorrect Verification Code $errorMsg", Toast.LENGTH_LONG
                        ).show()
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
        dialogbinding.edtRePassLogDia.visibility = View.INVISIBLE
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
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
        }

        val loginDialog = AlertDialog.Builder(this)
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
