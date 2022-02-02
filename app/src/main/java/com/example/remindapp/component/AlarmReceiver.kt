package com.example.remindapp.component

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.remindapp.component.AlarmService.Companion.ALARM_STATE
import com.example.remindapp.component.AlarmService.Companion.OFF
import com.example.remindapp.component.AlarmService.Companion.ON
import com.example.remindapp.model.repository.RemindLocalDatasource
import com.example.remindapp.model.repository.RemindRepository
import com.example.remindapp.model.room.RemindDatabase
import com.example.remindapp.util.SELECTED_REMIND_IDX
import com.example.remindapp.util.RemindManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) return
        val action = intent.action

        context?.let { tmpContext ->
            val remindIdx = intent.extras?.get(SELECTED_REMIND_IDX) ?: OFF
            if (Intent.ACTION_BOOT_COMPLETED == action || Intent.ACTION_LOCKED_BOOT_COMPLETED == action) {
                val result = goAsync()
                GlobalScope.launch(Dispatchers.IO) {
                    val repo = RemindRepository(RemindLocalDatasource(RemindDatabase.getInstance(tmpContext.applicationContext)))
                    repo.getAll().forEach { remind ->
                        if (remind.active) {
                            RemindManager.setPendingRemind(tmpContext, remind)
                        }
                    }
                    result.finish()
                }
            } else {
                if (remindIdx == OFF) return
                val serviceIntent = Intent(tmpContext, AlarmService::class.java).apply {
                    putExtra(ALARM_STATE, ON)
                    putExtra(SELECTED_REMIND_IDX, remindIdx as Int)
                }
                tmpContext.startService(serviceIntent)
            }
        }
    }
}
