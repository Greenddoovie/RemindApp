package com.example.remindapp.model.room

import androidx.room.*

@Dao
interface RemindDao {
    @Insert
    fun insert(remind: Remind)

    @Update
    fun update(remind: Remind)

    @Delete
    fun delete(remind: Remind)

    @Query("SELECT * FROM Remind")
    fun getAll(): List<Remind>
}