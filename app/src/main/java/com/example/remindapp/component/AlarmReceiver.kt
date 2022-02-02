package com.example.remindapp.component

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.remindapp.model.repository.RemindLocalDatasource
import com.example.remindapp.model.repository.RemindRepository
import com.example.remindapp.model.room.RemindDatabase
import com.example.remindapp.ui.home.HomeFragment
import com.example.remindapp.util.*

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) return
        val action = intent.action

        context?.let { tmpContext ->
            val remindIdx = intent.extras?.get(REMIND_IDX) ?: OFF
            if (remindIdx == OFF) return
            if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
                val repo = RemindRepository(RemindLocalDatasource(RemindDatabase.getInstance(tmpContext.applicationContext)))
                val reminds = repo.getAll()
                reminds.forEach { remind ->
                    if (remind.active) {
                        RemindManager.setPendingRemind(tmpContext, remind)
                    }
                }
            } else {
                val serviceIntent = Intent(tmpContext, AlarmService::class.java).apply {
                    putExtra(ALARM_STATE, ON)
                    putExtra(REMIND_IDX, remindIdx as Int)
                }
                tmpContext.startService(serviceIntent)
            }
        }
    }
}
