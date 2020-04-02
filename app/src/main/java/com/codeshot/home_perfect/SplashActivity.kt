package com.codeshot.home_perfect

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codeshot.home_perfect.ui.LoginActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

//        val thread: Thread = object : Thread() {
//            override fun run() {
//                try {
//                    sleep(3000)
//                    startActivity(Intent(this@SplashActivity,LoginActivity::class.java))
//                    finish()
//                } catch (e: InterruptedException) {
//                    e.printStackTrace()
//                }
//            }
//        }
//        thread.start()
        startActivity(Intent(this@SplashActivity,LoginActivity::class.java))
        finish()
    }


}
