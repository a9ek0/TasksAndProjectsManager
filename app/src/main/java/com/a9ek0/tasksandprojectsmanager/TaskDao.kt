package com.a9ek0.tasksandprojectsmanager

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE date = :date ORDER BY time")
    fun getTasksByDateAndTime(date: String): List<Task>

    @Query("SELECT COUNT(*) FROM tasks WHERE date = :date")
    fun countTasksByDate(date: String): Int

    @Query("SELECT * FROM tasks WHERE strftime('%m', date) = :month AND strftime('%Y', date) = :year ORDER BY time")
    fun getTasksByMonthAndYear(month: String, year: String): List<Task>

    @Query("SELECT * FROM tasks WHERE strftime('%d-%m-%Y', date) = :date ORDER BY time")
    fun getTasksByFormattedDate(date: String): List<Task>

    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun countAllTasks(): Int

    @Query("SELECT COUNT(*) FROM tasks WHERE completed = 1")
    suspend fun countCompletedTasks(): Int

    @Query("SELECT * FROM tasks WHERE date = :date")
    fun getTasksByDate(date: String): List<Task>

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): List<Task>

    @Query("SELECT * FROM tasks WHERE completed = 1")
    fun getCompletedTasks(): List<Task>

    @Delete
    suspend fun delete(task: Task)

    @Update
    suspend fun update(task: Task)

    @Insert
    fun insert(task: Task)
}