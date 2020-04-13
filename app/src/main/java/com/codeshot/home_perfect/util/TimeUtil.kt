package com.codeshot.home_perfect.util

import java.text.SimpleDateFormat
import java.util.*

object TimeUtil {
    private const val DATE_FORMAT = "EEEE, MMMM d, yyyy - h:mm a"
    fun getDefaultTimeText(unixTime: Long, timeZone: TimeZone?): String {
        val date = Date(unixTime)
        val simpleDateFormat =
            SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        simpleDateFormat.timeZone = timeZone
        return simpleDateFormat.format(date)
    }
}