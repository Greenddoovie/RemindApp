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
}