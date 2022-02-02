package com.example.remindapp.component

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.remindapp.model.repository.RemindLocalDatasource
import com.example.remindapp.model.repository.RemindRepository
import com.example.remindapp.model.room.RemindDatabase
import com.example.remindapp.util.*

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) return
        val action = intent.action

        context?.let { tmpContext ->
            println("Result Broadcast Receiver is called")
            val remindIdx = intent.extras?.get(REMIND_IDX) ?: OFF
            println("Result Broadcast Receiver : $remindIdx")
            println("Result Action: ${intent.action}")
            println("Result Action Boolean: ${Intent.ACTION_BOOT_COMPLETED == action}")
            if (Intent.ACTION_BOOT_COMPLETED == action || Intent.ACTION_LOCKED_BOOT_COMPLETED == action) {
                val repo = RemindRepository(RemindLocalDatasource(RemindDatabase.getInstance(tmpContext.applicationContext)))
                repo.getAll().forEach { remind ->
                    if (remind.active) {
                        RemindManager.setPendingRemind(tmpContext, remind)
                    }
                }
                println("Result boot complete is called")
            } else {
                if (remindIdx == OFF) return
                val serviceIntent = Intent(tmpContext, AlarmService::class.java).apply {
                    putExtra(ALARM_STATE, ON)
                    putExtra(REMIND_IDX, remindIdx as Int)
                }
                tmpContext.startService(serviceIntent)
                println("Result intent is called")
            }
        }
    }
}
