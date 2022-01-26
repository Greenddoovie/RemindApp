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

}