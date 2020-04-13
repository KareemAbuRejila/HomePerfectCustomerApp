package com.codeshot.home_perfect.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtil {
    fun requestPermissions(
        activity: Activity?,
        permission: String,
        requestCode: Int
    ) {
        ActivityCompat.requestPermissions(activity!!, arrayOf(permission), requestCode)
    }

    fun requestPermissions(
        activity: Activity?,
        permissions: Array<String>,
        requestCode: Int
    ) {
        ActivityCompat.requestPermissions(activity!!, permissions, requestCode)
    }

    fun isPermissionGranted(
        permission: String?,
        context: Context?
    ): Boolean {
        return (ContextCompat.checkSelfPermission(context!!, permission!!)
                == PackageManager.PERMISSION_GRANTED)
    }
}