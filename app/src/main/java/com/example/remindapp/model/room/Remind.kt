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
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    fun convertTime() =
        "${if (hour < 10) "0$hour" else "$hour"}:${if (minute < 10) "0$minute" else "$minute"} ${if (hour >= 12) "PM" else "AM"}"

}