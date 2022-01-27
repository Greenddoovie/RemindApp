package com.example.remindapp.util

import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

fun getCurrentTime(): List<Int> {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()).split(":").map { it.toInt() }
}

private fun getCurrentDay(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}

private fun getTargetTime(hour: Int, minute: Int): String {
    return "$hour:$minute:00"
}

fun convertDateToMillis(hour: Int, minute: Int, flag: Boolean): Long {
    val date = getTime(hour, minute)
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

private fun getTime(hour: Int, minute: Int): String {
    return "${getCurrentDay()} ${getTargetTime(hour, minute)}"
}
