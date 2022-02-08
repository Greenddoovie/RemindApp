package com.example.remindapp.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.remindapp.component.AlarmReceiver
import com.example.remindapp.model.room.Remind
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

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
            putExtra(SELECTED_REMIND_IDX, remind.id)
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
    }

    private fun checkRemindAlarmOnNextDayOrNot(remind: Remind): Boolean {
        val (curHour, curMin) = getCurrentTime()
        return if (remind.hour < curHour) { true } else remind.hour == curHour && remind.minute <= curMin
    }

    private fun convertDateToMillis(hour: Int, minute: Int, flag: Boolean): Long {
        val date = convertTime(hour, minute)
        val pattern = "yyyy-MM-dd HH:mm:ss"
        val parser = SimpleDateFormat(pattern, Locale.getDefault())
        val result = try {
            parser.parse(date).time
        } catch (e: Exception) {
            e.printStackTrace()
            -1L
        }
        return if(flag) result + DAY_MILLIS else result
    }

    private fun getCurrentTime() =
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()).split(":").map { it.toInt() }

    private fun getCurrentDay() =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private fun getTargetTime(hour: Int, minute: Int) = "$hour:$minute:00"

    private fun convertTime(hour: Int, minute: Int): String =
        "${getCurrentDay()} ${getTargetTime(hour, minute)}"

    private const val DAY_MILLIS = 86400000L
}