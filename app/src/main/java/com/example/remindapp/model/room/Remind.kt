package com.example.remindapp.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Remind(
    val title: String,
    val hour: Int,
    val minute: Int,
    val uri: String,
    val active: Boolean
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0

    fun convertTime(): String {
        val hourStr = if (hour < 10) "0$hour" else "$hour"
        val minuteStr = if (minute < 10) "0$minute" else "$minute"
        val apm = if (hour >= 12) "PM" else "AM"
        return "$hourStr:$minuteStr $apm"
    }
}