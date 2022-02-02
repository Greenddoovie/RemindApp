package com.example.remindapp.model.repository

import com.example.remindapp.model.room.Remind

class RemindRepository(private val remindLocalDatasource: IRemindRepo) : IRemindRepo {
    override fun getAll(): List<Remind> {
        return remindLocalDatasource.getAll()
    }

    override fun insert(remind: Remind) {
        remindLocalDatasource.insert(remind)
    }

    override fun update(remind: Remind) {
        remindLocalDatasource.update(remind)
    }

    override fun update(
        title: String,
        hour: Int,
        minute: Int,
        uri: String,
        active: Boolean,
        id: Int
    ) {
        remindLocalDatasource.update(title, hour, minute, uri, active, id)
    }

    override fun getRemind(idx: Int): Remind {
        return remindLocalDatasource.getRemind(idx)
    }

    override fun getRemind(title: String, hour: Int, minute: Int, uri: String): Remind {
        return remindLocalDatasource.getRemind(title, hour, minute, uri)
    }

}