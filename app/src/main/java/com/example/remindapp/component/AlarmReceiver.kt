package com.example.remindapp.component

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.remindapp.model.repository.RemindLocalDatasource
import com.example.remindapp.model.repository.RemindRepository
import com.example.remindapp.model.room.RemindDatabase
import com.example.remindapp.util.setAlarm

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) return
        val action = intent.action

        context?.let {
            if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
                val repo = RemindRepository(RemindLocalDatasource(RemindDatabase.getInstance(it)))
                val reminds = repo.getAll()
                setAlarm(reminds, it)
            } else {
                val remindIdx = intent?.extras?.get("remindIdx") ?: -1

                val serviceIntent = Intent(it, AlarmService::class.java).apply {
                    putExtra("alarm", "on")
                    putExtra("remindIdx", remindIdx as Int)
                }

                it.startService(serviceIntent)
            }
        }
    }
}
