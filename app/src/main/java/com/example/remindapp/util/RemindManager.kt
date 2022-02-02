package com.example.remindapp.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.remindapp.component.AlarmReceiver
import com.example.remindapp.model.room.Remind

object RemindManager {

    fun cancelRemind(context: Context, remind: Remind) {
        val pending = PendingIntent.getBroadcast(
            context,
            remind.id,
            Intent(context, AlarmReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pending?.cancel()
    }

    fun setPendingRemind(context: Context, remind: Remind) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val dayPlus = checkRemindAlarmOnNextDayOrNot(remind)
        val pending = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(REMIND_IDX, remind.id)
        }.run {
            PendingIntent.getBroadcast(
                context,
                remind.id,
                this,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val curMillis = convertDateToMillis(remind.hour, remind.minute, dayPlus)
        if (curMillis == -1L) return

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, curMillis, pending)
        println("Result ${remind}, Result idx: ${remind.id}")
    }

    private fun checkRemindAlarmOnNextDayOrNot(remind: Remind): Boolean {
        val (curHour, curMin) = getCurrentTime()
        return if (remind.hour < curHour) { true } else remind.hour == curHour && remind.minute <= curMin
    }

}