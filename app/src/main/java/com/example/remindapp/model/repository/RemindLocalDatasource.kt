package com.example.remindapp.model.repository

import com.example.remindapp.model.room.Remind
import com.example.remindapp.model.room.RemindDatabase

class RemindLocalDatasource(private val remindDatabase: RemindDatabase) : IRemindRepo {
    override fun getAll(): List<Remind> {
        return remindDatabase.remindDao().getAll()
    }

    override fun insert(remind: Remind) {
        remindDatabase.remindDao().insert(remind)
    }

    override fun update(remind: Remind) {
        remindDatabase.remindDao().update(remind)
    }

}
