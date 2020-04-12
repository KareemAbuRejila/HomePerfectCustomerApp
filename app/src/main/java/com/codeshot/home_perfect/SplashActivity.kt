package com.codeshot.home_perfect

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codeshot.home_perfect.ui.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val thread: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(3000)
                    isLogined()

                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
        thread.start()
    }

    private fun sendToHomeActivity(type: String) {
        val homeIntent = Intent(this, HomeActivity::class.java)
        homeIntent.putExtra("user", type)
        startActivity(homeIntent)
        finish()
    }

    fun sendToLoginActivity() {
        startActivity(Intent(this@SplashActivity,LoginActivity::class.java))
        finish()
    }

    private fun isLogined() {
        if (FirebaseAuth.getInstance().currentUser != null)
            sendToHomeActivity("old")
        else
            sendToLoginActivity()
    }

}
