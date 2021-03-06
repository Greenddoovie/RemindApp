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
import com.example.remindapp.util.*
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

        val idx = intent.extras?.get(SELECTED_REMIND_IDX) ?: -1
        val onOff = intent.extras?.get(ALARM_STATE) ?: OFF

        if (onOff == ON) {
            repo =
                RemindRepository(RemindLocalDatasource(RemindDatabase.getInstance(applicationContext)))
            CoroutineScope(Dispatchers.IO).launch {
                val remind = repo.getRemind(idx as Int)

                mediaPlayer = MediaPlayer.create(applicationContext, remind.uri.toUri())
                mediaPlayer.start()
                mediaPlayer.isLooping = true
                mediaPlayer.setVolume(1.0F, 1.0F)

                withContext(Dispatchers.Main) {
                    Intent(applicationContext, MainActivity::class.java).run {
                        putExtra(SELECTED_REMIND_IDX, remind.id)
                        this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        applicationContext.startActivity(this)
                    }
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

    companion object {
        const val ALARM_STATE = "alarm"
        const val ON = 1
        const val OFF = 0
    }

}