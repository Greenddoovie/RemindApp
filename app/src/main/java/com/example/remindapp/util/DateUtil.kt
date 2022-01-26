package com.example.remindapp.util

import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

fun getCurrentTime(): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
}

fun getCurrentDay(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}

fun getTargetTime(hour: Int, minute: Int): String {
    return "$hour:$minute:00"
}

fun convertDateToMillis(date: String, flag: Boolean): Long {
    val pattern = "yyyy-MM-dd HH:mm:ss"
    val parser = SimpleDateFormat(pattern, Locale.getDefault())
    val result = try {
        parser.parse(date).time
    } catch (e: Exception) {
        e.printStackTrace()
        -1L
    }
    return if(flag) result + 86400000L else result
}
