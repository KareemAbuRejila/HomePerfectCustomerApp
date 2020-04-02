package com.codeshot.home_perfect.ui

import android.content.DialogInterface
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
import com.codeshot.home_perfect.Common.Common.USERS_REF
import com.codeshot.home_perfect.HomeActivity
import com.codeshot.home_perfect.R
import com.codeshot.home_perfect.databinding.ActivityLoginBinding
import com.codeshot.home_perfect.databinding.DialogLoginBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit


class LoginActivity : AppCompatActivity() {
    private val TAG:String="LOGIN ACTIVITY"
    private lateinit var activityLoginBinding: ActivityLoginBinding
    private lateinit var progressBar: ACProgressBaseDialog
    private var codeSent:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isLogined()
        activityLoginBinding=DataBindingUtil.setContentView(this,
            R.layout.activity_login
        )
        activityLoginBinding.ccpLogin.registerPhoneNumberTextView(activityLoginBinding.edtPhoneLogin)
        progressBar= ACProgressFlower.Builder(this)
            .direction(ACProgressConstant.DIRECT_CLOCKWISE)
            .themeColor(Color.WHITE)
            .text("Please Wait ....!")
            .fadeColor(Color.DKGRAY).build()
        activityLoginBinding.edtPhoneLogin
            .setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                return@OnEditorActionListener true
            }
            false
        })
        activityLoginBinding.btnConLogin.setOnClickListener(View.OnClickListener { v: View? ->
            sendVerificationCode()
        })
        activityLoginBinding.codeInput.addOnCompleteListener { code: String? ->
            verifySignInCode()
        }

    }

    private fun isLogined() {
        if (FirebaseAuth.getInstance().currentUser!=null){
            sendToHomeActivity("old")
        }else
            return
    }

    private fun sendVerificationCode() {
        val phoneNamber=activityLoginBinding.ccpLogin.fullNumberWithPlus
        Toast.makeText(this@LoginActivity,
            phoneNamber,
            Toast.LENGTH_SHORT).show()
        if (phoneNamber.isEmpty()) {
            activityLoginBinding.edtPhoneLogin.error = "Phone Number is required  "
        } else if (phoneNamber.length < 10) {
            activityLoginBinding.edtPhoneLogin.error = "Check your Phone Number "
        } else {
            progressBar.show()
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNamber,  // Phone number to verify
                60,  // Timeout duration
                TimeUnit.SECONDS,  // Unit of timeout
                this,  // Activity (for callback binding)
                mCallbacks
            ) // OnVerificationStateChangedCallbacks
        }
    }

    private val mCallbacks: OnVerificationStateChangedCallbacks =
        object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
                Log.e(TAG,e.message)
                progressBar.hide()
            }

            override fun onCodeSent(s: String,forceResendingToken: ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                progressBar.hide()
                showCodeInput()
                codeSent = s
            }
        }
    private fun showCodeInput(){
        activityLoginBinding.btnConLogin.visibility=View.GONE
        activityLoginBinding.phoneLayoutLogin.visibility=View.GONE
        activityLoginBinding.textView3.visibility=View.GONE
        activityLoginBinding.codeInput.visibility=View.VISIBLE

        back=false

    }
    private fun hideCodeInput(){
        activityLoginBinding.codeInput.visibility=View.GONE
        activityLoginBinding.btnConLogin.visibility=View.VISIBLE
        activityLoginBinding.phoneLayoutLogin.visibility=View.VISIBLE
        activityLoginBinding.textView3.visibility=View.VISIBLE
    }
    private fun verifySignInCode() {
        val code: String = activityLoginBinding.codeInput.code.toString()
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(
                this@LoginActivity,
                "Code IS Empty", Toast.LENGTH_LONG
            ).show()
        } else {
            progressBar.show()
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
                        " "+"IS Logined",
                        Toast.LENGTH_LONG)
                    sendToHomeActivity("new")
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        val errorMsg = task.exception.toString()
                        progressBar.hide()
                        Toast.makeText(
                            this@LoginActivity,
                            "Incorrect Verification Code $errorMsg", Toast.LENGTH_LONG
                        ).show()
                        Log.e(TAG,errorMsg)
                    }
                }
            }
    }

    fun showLoginDialog(view: View) {
        val alertDialog=
            AlertDialog.Builder(this)
                .setPositiveButton("Login",
                    DialogInterface.OnClickListener { dialog, which ->
                        LoginDialog("in")
        }).setNegativeButton("SignUp", DialogInterface.OnClickListener { dialog, which ->
                    SignUpDialog("up")
                }).create()
        alertDialog.show()
    }

    private fun SignUpDialog(s: String) {
        val dialogbinding=DialogLoginBinding.inflate(layoutInflater,
            activityLoginBinding.root as ViewGroup?,false)
        dialogbinding.btnLogin.text="SignUp"
        dialogbinding.btnLogin.setOnClickListener {
            val email=dialogbinding.edtEmailLogDia.text.toString()
            val pass=dialogbinding.edtPassLogDia.text.toString()
            val repass=dialogbinding.edtRePassLogDia.text.toString()
            if (pass == repass){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,pass)
                    .addOnSuccessListener {
                        sendToHomeActivity("new")
                    }
            }
        }

        val signupDiaog=AlertDialog.Builder(this)
            .setView(dialogbinding.root).create()
        signupDiaog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        signupDiaog.show()
    }

    private fun LoginDialog(s: String) {
        val dialogbinding=DialogLoginBinding.inflate(layoutInflater,
            activityLoginBinding.root as ViewGroup?,false)
        dialogbinding.edtRePassLogDia.visibility=View.INVISIBLE
        dialogbinding.btnLogin.text="Login"
        dialogbinding.btnLogin.setOnClickListener {
            val email=dialogbinding.edtEmailLogDia.text.toString()
            val pass=dialogbinding.edtPassLogDia.text.toString()
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,pass)
                .addOnSuccessListener {
                    sendToHomeActivity("new")
                }.addOnFailureListener {
                    Toast.makeText(this,it.message,Toast.LENGTH_LONG).show()
                }
        }

        val loginDialog=AlertDialog.Builder(this)
            .setView(dialogbinding.root).create()
        loginDialog.show()
        loginDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


    }

    private fun sendToHomeActivity(type:String){
        val homeIntent = Intent(this, HomeActivity::class.java)
        homeIntent.putExtra("user", type)
        startActivity(homeIntent)
        finish()
    }
    private var back=true
    override fun onBackPressed() {
        if (!back){
            hideCodeInput()
        }else
        super.onBackPressed()
    }


}
