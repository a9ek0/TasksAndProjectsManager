package com.a9ek0.tasksandprojectsmanager

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Task::class, Project::class], version = 5) // Incremented version number
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun projectDao(): ProjectDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "task_database"
                )
                    .addMigrations(MIGRATION_4_5) // Add the new migration
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Drop the old tables
                database.execSQL("DROP TABLE IF EXISTS `tasks`")
                database.execSQL("DROP TABLE IF EXISTS `projects`")

                // Create the new `projects` table with the correct schema
                database.execSQL("""
            CREATE TABLE IF NOT EXISTS `projects` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `description` TEXT NOT NULL
            )
        """.trimIndent())

                // Create the new `tasks` table with the correct schema
                database.execSQL("""
            CREATE TABLE IF NOT EXISTS `tasks` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `title` TEXT NOT NULL,
                `description` TEXT NOT NULL,
                `date` TEXT NOT NULL,
                `time` TEXT NOT NULL,
                `duration` INTEGER NOT NULL DEFAULT 1,
                `projectId` INTEGER,
                `projectName` TEXT NOT NULL,
                FOREIGN KEY(`projectId`) REFERENCES `projects`(`id`) ON DELETE CASCADE
            )
        """.trimIndent())
            }
        }    }

    fun clearDatabase() {
        INSTANCE?.clearAllTables()
    }
}