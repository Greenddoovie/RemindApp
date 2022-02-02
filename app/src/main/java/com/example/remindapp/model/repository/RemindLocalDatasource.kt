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

    override fun update(
        title: String,
        hour: Int,
        minute: Int,
        uri: String,
        active: Boolean,
        id: Int
    ) {
        remindDatabase.remindDao().update(title, hour, minute, uri, active, id)
    }

    override fun getRemind(idx: Int): Remind {
        return remindDatabase.remindDao().getRemind(idx)
    }

    override fun getRemind(title: String, hour: Int, minute: Int, uri: String): Remind {
        return remindDatabase.remindDao().getRemind(title, hour, minute, uri)
    }

}
