package com.example.remindapp.component

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.example.remindapp.MainActivity
import com.example.remindapp.R
import com.example.remindapp.model.repository.RemindLocalDatasource
import com.example.remindapp.model.repository.RemindRepository
import com.example.remindapp.model.room.RemindDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlarmService : Service() {

    private lateinit var repo: RemindRepository
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(applicationContext)
            notifyNotification(applicationContext)
        }
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) return START_NOT_STICKY

        val idx = intent.extras?.get("remindIdx") ?: -1
        val onOff = intent.extras?.get("alarm") ?: "off"

        if (onOff == "on") {
            repo = RemindRepository(RemindLocalDatasource(RemindDatabase.getInstance(applicationContext)))
            CoroutineScope(Dispatchers.IO).launch {
                val remind = repo.getRemind(idx as Int)
                mediaPlayer = MediaPlayer.create(applicationContext, remind.uri.toUri())
                mediaPlayer.start()
                mediaPlayer.isLooping = true

                withContext(Dispatchers.Main) {
                    val activityIntent = Intent(applicationContext, MainActivity::class.java)
                    activityIntent.putExtra("remindIdx", remind.id)
                    activityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    applicationContext.startActivity(activityIntent)
                }

            }

            return START_NOT_STICKY
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        mediaPlayer.stop()
        mediaPlayer.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "리마인드 알람",
                NotificationManager.IMPORTANCE_HIGH
            )

            NotificationManagerCompat.from(context).createNotificationChannel(notificationChannel)
        }
    }

    private fun notifyNotification(context: Context) {
        val build = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentText("알람")
            .setContentText("리마인드 시간")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.alarm_24)
        startForeground(NOTIFICATION_ID, build.build())
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "1000"
        const val NOTIFICATION_ID = 100
    }

}