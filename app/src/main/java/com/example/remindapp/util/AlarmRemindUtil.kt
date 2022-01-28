package com.example.remindapp.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.remindapp.component.AlarmReceiver
import com.example.remindapp.model.room.Remind
import com.example.remindapp.ui.home.HomeFragment

private fun filterActiveGreaterThanCurTime(reminds: List<Remind>): List<Remind> {
    val (currentHour, currentMin) = getCurrentTime()
    return reminds.filter { it.active }.filter { it.hour >= currentHour }.filter { it.minute > currentMin }
}

fun findTarget(reminds: List<Remind>): Pair<Remind?, Boolean> {
    val filtered = filterActiveGreaterThanCurTime(reminds)
    return if (filtered.isNullOrEmpty()) {
        reminds.firstOrNull { it.active } to true
    } else {
        filtered.firstOrNull() to false
    }
}

fun setAlarm(remindList: List<Remind>, context: Context) {
    if (remindList.isEmpty()) return

    val (target, dayPlus) = findTarget(remindList)
    if (target == null) return

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val pending = Intent(context, AlarmReceiver::class.java).apply {
        putExtra(REMIND_IDX, target.id)
    }.run {
        PendingIntent.getBroadcast(
            context,
            HomeFragment.ALARM_REQUEST_CODE,
            this,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
    val curMillis = convertDateToMillis(target.hour, target.minute, dayPlus)
    if (curMillis == -1L) return

    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, curMillis, pending)
}