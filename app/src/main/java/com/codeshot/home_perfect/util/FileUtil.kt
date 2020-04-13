package com.codeshot.home_perfect.util

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.text.TextUtils
import com.codeshot.home_perfect.common.Common
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object FileUtil {
    fun createImageFile(context: Context): File? {
        val imageFile: File
        imageFile = try {
            // Create an image file name
            val timeStamp =
                SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                    .format(Date())
            val imageFileName = "TRAVEL_BUDDY_" + timeStamp + "_"
            val storageDir =
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
            )
        } catch (exception: IOException) {
            return null
        }
        return imageFile
    }

    fun deleteCameraImageWithUri(uri: Uri?) {
        if (uri == null) {
            return
        }
        val uriString = uri.toString()
        if (TextUtils.isEmpty(uriString)) {
            return
        }
        val filePath = uriString.substring(uriString.lastIndexOf('/'))
        val completePath =
            (Environment.getExternalStorageDirectory().path
                    + Common.FILE_PROVIDER_PATH
                    + filePath)
        val imageFile = File(completePath)
        if (imageFile.exists()) {
            imageFile.delete()
        }
    }
}