package com.a9ek0.tasksandprojectsmanager

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects")
    fun getAllProjects(): List<Project>

    @Query("SELECT COUNT(*) FROM projects")
    suspend fun countAllProjects(): Int

    @Insert
    fun insert(project: Project): Long
}