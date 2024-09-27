package com.a9ek0.tasksandprojectsmanager

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE date = :date ORDER BY time")
    fun getTasksByDateAndTime(date: String): List<Task>

    @Query("SELECT COUNT(*) FROM tasks WHERE date = :date")
    fun countTasksByDate(date: String): Int

    @Insert
    fun insert(task: Task)
}