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

    @Query("SELECT * FROM Remind WHERE id = :idx")
    fun getRemind(idx: Int): Remind

    @Query("SELECT * FROM Remind WHERE title =:title AND hour =:hour AND minute = :minute AND uri = :uri")
    fun getRemind(title: String, hour: Int, minute: Int, uri: String): Remind

    @Query("UPDATE Remind SET title = :title, hour = :hour, minute = :minute, uri = :uri, active = :active WHERE id = :id ")
    fun update(title: String, hour: Int, minute: Int, uri: String, active: Boolean, id: Int)
}