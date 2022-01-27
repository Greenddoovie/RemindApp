package com.example.remindapp.component

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import androidx.core.net.toUri
import com.example.remindapp.MainActivity
import com.example.remindapp.model.repository.RemindLocalDatasource
import com.example.remindapp.model.repository.RemindRepository
import com.example.remindapp.model.room.RemindDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlarmService : Service() {

    private val binder = AlarmBinder()
    private lateinit var repo: RemindRepository
    private lateinit var mediaPlayer: MediaPlayer

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) return START_NOT_STICKY

        val idx = intent.extras?.get("remindIdx") ?: -1
        val onOff = intent.extras?.get("alarm") ?: "off"

        if (onOff == "on") {
            repo =
                RemindRepository(RemindLocalDatasource(RemindDatabase.getInstance(applicationContext)))
            CoroutineScope(Dispatchers.IO).launch {
                val remind = repo.getRemind(idx as Int)

                mediaPlayer = MediaPlayer.create(applicationContext, remind.uri.toUri())
                mediaPlayer.start()
                mediaPlayer.isLooping = true

                withContext(Dispatchers.Main) {
                    val activityIntent = Intent(applicationContext, MainActivity::class.java)
                    activityIntent.putExtra("remindIdx", remind.id)
                    activityIntent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    fun stopService() {
        stopSelf()
    }

    inner class AlarmBinder : Binder() {
        fun getService(): AlarmService = this@AlarmService
    }

}