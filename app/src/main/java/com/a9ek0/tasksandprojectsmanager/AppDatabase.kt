package com.a9ek0.tasksandprojectsmanager

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Task::class], version = 2) // Increment the version number
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

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
                    .addMigrations(MIGRATION_1_2) // Add migration strategy if needed
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Check if the 'time' column exists in the old 'tasks' table
                val cursor = database.query("PRAGMA table_info(tasks)")
                var timeColumnExists = false
                while (cursor.moveToNext()) {
                    val columnName = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    if (columnName == "time") {
                        timeColumnExists = true
                        break
                    }
                }
                cursor.close()

                // If the 'time' column does not exist, add it to the old 'tasks' table
                if (!timeColumnExists) {
                    database.execSQL("ALTER TABLE `tasks` ADD COLUMN `time` TEXT NOT NULL DEFAULT ''")
                }

                // Create the new table with the correct schema
                database.execSQL("""
            CREATE TABLE IF NOT EXISTS `tasks_new` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `title` TEXT NOT NULL,
                `description` TEXT NOT NULL,
                `date` TEXT NOT NULL,
                `time` TEXT NOT NULL
            )
        """.trimIndent())

                // Copy the data from the old table to the new table
                database.execSQL("""
            INSERT INTO `tasks_new` (`id`, `title`, `description`, `date`, `time`)
            SELECT `id`, `title`, `description`, `date`, `time`
            FROM `tasks`
        """.trimIndent())

                // Remove the old table
                database.execSQL("DROP TABLE `tasks`")

                // Rename the new table to the old table name
                database.execSQL("ALTER TABLE `tasks_new` RENAME TO `tasks`")
            }
        }
    }
    fun clearDatabase() {
        INSTANCE?.clearAllTables()
    }
}