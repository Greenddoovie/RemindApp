package com.example.remindapp.component

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.remindapp.model.repository.RemindLocalDatasource
import com.example.remindapp.model.repository.RemindRepository
import com.example.remindapp.model.room.RemindDatabase
import com.example.remindapp.ui.home.HomeFragment
import com.example.remindapp.util.ALARM_STATE
import com.example.remindapp.util.REMIND_IDX
import com.example.remindapp.util.setAlarm

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) return
        val action = intent.action

        context?.let {
            if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
                val repo = RemindRepository(RemindLocalDatasource(RemindDatabase.getInstance(it.applicationContext)))
                val reminds = repo.getAll()
                cancelAlarm(it.applicationContext)
                setAlarm(reminds, it.applicationContext)
            } else {
                val remindIdx = intent.extras?.get(REMIND_IDX) ?: -1

                val serviceIntent = Intent(it, AlarmService::class.java).apply {
                    putExtra(ALARM_STATE, "on")
                    putExtra(REMIND_IDX, remindIdx as Int)
                }

                it.startService(serviceIntent)
            }
        }
    }

    private fun cancelAlarm(context: Context) {
        val pending = PendingIntent.getBroadcast(
            context,
            HomeFragment.ALARM_REQUEST_CODE,
            Intent(context, AlarmReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pending?.cancel()
    }
}
