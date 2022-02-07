package com.example.remindapp.model.repository

import com.example.remindapp.model.room.Remind

interface IRemindRepo {

    fun getAll(): List<Remind>
    fun insert(remind: Remind)
    fun update(remind: Remind)
    fun update(title: String, hour: Int, minute: Int, uri: String, active: Boolean, id: Int)
    fun getRemind(idx: Int): Remind
    fun getRemind(title: String, hour: Int, minute: Int, uri: String): Remind

}