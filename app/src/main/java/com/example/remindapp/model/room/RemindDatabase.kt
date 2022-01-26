package com.example.remindapp.model.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Remind::class], version = 1, exportSchema = false)
abstract class RemindDatabase : RoomDatabase() {
    abstract fun remindDao(): RemindDao

    companion object {
        private var instance: RemindDatabase? = null

        fun getInstance(context: Context): RemindDatabase {
            if (instance == null) {
                synchronized(RemindDatabase::class) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(
                            context.applicationContext,
                            RemindDatabase::class.java,
                            "remind-database"
                        ).build()
                    }
                }
            }
            return instance!!
        }
    }
}