package com.example.remindapp.model.repository

import com.example.remindapp.model.room.Remind

interface IRemindRepo {

    fun getAll(): List<Remind>
    fun insert(remind: Remind)
    fun update(remind: Remind)

}