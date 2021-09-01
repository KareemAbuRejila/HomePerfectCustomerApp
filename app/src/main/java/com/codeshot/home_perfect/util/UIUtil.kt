package com.codeshot.home_perfect.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes

object UIUtil {
    fun closeKeyboard(activity: Activity) {
        val inputMethodManager = activity
            .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            ?: return
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showShortToast(@StringRes stringId: Int, context: Context) {
        showToast(stringId, Toast.LENGTH_SHORT, context)
    }

    fun showLongToast(@StringRes stringId: Int, context: Context) {
        showToast(stringId, Toast.LENGTH_LONG, context)
    }

    private fun showToast(
        @StringRes stringId: Int,
        duration: Int,
        context: Context
    ) {
        if (context != null)
            Toast.makeText(context, stringId, duration).show()
    }

    fun showShortToast(text: String, context: Context) {
        showToast(text, Toast.LENGTH_SHORT, context)
    }

    fun showLongToast(text: String, context: Context) {
        showToast(text, Toast.LENGTH_LONG, context)
    }

    private fun showToast(
        text: String,
        duration: Int,
        context: Context
    ) {
        if (context != null)
            Toast.makeText(context, text, duration).show()
    }
}