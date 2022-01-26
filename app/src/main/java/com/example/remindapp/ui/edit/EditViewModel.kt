package com.example.remindapp.ui.edit

import androidx.lifecycle.ViewModel

class EditViewModel : ViewModel() {

    fun saveAlarm(title: String, uri: String, hour: Int, minute: Int) {
        println("title: $title\turi: $uri\thour: $hour\tminute: $minute")
    }
}