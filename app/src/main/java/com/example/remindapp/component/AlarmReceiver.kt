package com.example.remindapp.component

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val remindIdx = intent?.extras?.get("remindIdx") ?: -1

            val serviceIntent = Intent(it, AlarmService::class.java).apply {
                putExtra("alarm", "on")
                putExtra("remindIdx", remindIdx as Int)
            }

            it.startService(serviceIntent)
        }
    }
}
