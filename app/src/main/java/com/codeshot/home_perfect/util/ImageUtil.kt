package com.codeshot.home_perfect.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ImageUtil {
    private const val MAX_IMAGE_HEIGHT = 1024
    private const val MAX_IMAGE_WIDTH = 1024
    private fun getBitmapFromFileProviderUri(
        context: Context,
        takenPhotoUri: Uri
    ): Bitmap? {
        val contentResolver = context.contentResolver
        return try {
            // First decode with inJustDecodeBounds=true to check dimensions
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            var imageStream = contentResolver.openInputStream(takenPhotoUri)
            BitmapFactory.decodeStream(imageStream, null, options)
            imageStream!!.close()

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(
                options,
                MAX_IMAGE_WIDTH,
                MAX_IMAGE_HEIGHT
            )

            // Decode bitmap with inSampleSize set, capping it at 1024x1024 and decreasing chances of OOM
            options.inJustDecodeBounds = false
            imageStream = contentResolver.openInputStream(takenPhotoUri)
            BitmapFactory.decodeStream(imageStream, null, options)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Calculate an inSampleSize for use in a [BitmapFactory.Options] object when decoding
     * bitmaps using the decode* methods from [BitmapFactory]. This implementation calculates
     * the closest inSampleSize that will result in the final decoded bitmap having a width and
     * height equal to or larger than the requested width and height. This implementation does not
     * ensure a power of 2 is returned for inSampleSize which can be faster when decoding but
     * results in a larger bitmap which isn't as useful for caching purposes.
     *
     * @param options   An options object with out* params already populated (run through a decode*
     * method with inJustDecodeBounds==true
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            val heightRatio =
                Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio =
                Math.round(width.toFloat() / reqWidth.toFloat())

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).
            val totalPixels = width * height.toFloat()

            // Anything more than 2x the requested pixels we'll sample down further
            val totalReqPixelsCap = reqWidth * reqHeight * 2.toFloat()
            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++
            }
        }
        return inSampleSize
    }

    @Throws(IOException::class)
    fun rotateImageIfRequired(
        context: Context,
        photoFile: File?,
        takenPhotoUri: Uri
    ): Bitmap? {
        val input =
            context.contentResolver.openInputStream(takenPhotoUri)
        val exifInterface =
            ExifInterface(input!!)
        val orientation = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        val rotatedBitmap: Bitmap
        rotatedBitmap = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(context, takenPhotoUri, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(context, takenPhotoUri, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(context, takenPhotoUri, 270)
            else -> return getBitmapFromFileProviderUri(
                context,
                takenPhotoUri
            )
        }
        if (rotatedBitmap != null) {
            val out = FileOutputStream(photoFile)
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, out)
            out.flush()
            out.close()
        }
        return rotatedBitmap
    }

    private fun rotateImage(
        context: Context,
        takenPhotoUri: Uri,
        degree: Int
    ): Bitmap {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        val bitmap = getBitmapFromFileProviderUri(
            context,
            takenPhotoUri
        )
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedImg = Bitmap.createBitmap(
            bitmap!!, 0, 0, bitmap.width, bitmap.height, matrix, true
        )
        bitmap.recycle()
        return rotatedImg
    }
}