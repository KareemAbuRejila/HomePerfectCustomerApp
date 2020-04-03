package com.codeshot.home_perfect.common

import android.content.Intent
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.codeshot.home_perfect.R


open class StandardActivity : AppCompatActivity() {

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out)
    }

    override fun startActivityForResult(intent: Intent, requestCode: Int) {
//        UIUtil.closeKeyboard(this)
        super.startActivityForResult(intent, requestCode)
        overridePendingTransition(R.anim.slide_left_out, R.anim.slide_left_in)
    }

    override fun finish() {
//        UIUtil.closeKeyboard(this)
        super.finish()
        overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}