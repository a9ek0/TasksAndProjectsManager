package com.a9ek0.tasksandprojectsmanager

import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var taskDao: TaskDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getSupportActionBar() != null) {
            getSupportActionBar()?.hide();
        }
        setContentView(R.layout.activity_main)

        val calendarIcon: ImageView = findViewById(R.id.calendar_icon)
        calendarIcon.setOnClickListener {
            val intent = Intent(this, TaskListActivity::class.java)
            startActivity(intent)
            finish()
        }

        val profileIcon: ImageView = findViewById(R.id.profile_icon)
        profileIcon.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }


        taskDao = AppDatabase.getDatabase(this).taskDao()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val dateTextView: TextView = findViewById(R.id.date)
        val currentDate = SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault()).format(Date())
        dateTextView.text = currentDate

        val tasksCountTextView: TextView = findViewById(R.id.tasks_count)
        val tasksCount = getTasksCount()
        tasksCountTextView.text = tasksCount.toString()

        val progressBar: ProgressBar = findViewById(R.id.progress_bar)
        val progressTextView: TextView = findViewById(R.id.progress_text)
        val progressPercentage = calculateProgress(tasksCount, getCompletedTasksCount())
        progressBar.progress = progressPercentage
        progressTextView.text = "$progressPercentage%"

        lifecycleScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val count = withContext(Dispatchers.IO) {
                taskDao.countTasksByDate(today)
            }
            val tasksCountTextView: TextView = findViewById(R.id.tasks_count)
            tasksCountTextView.text = count.toString()
        }
    }

    private fun getTasksCount(): Int {
        return 5
    }

    private fun getCompletedTasksCount(): Int {
        return 3 // Example value, replace with actual logic
    }

    private fun calculateProgress(totalTasks: Int, completedTasks: Int): Int {
        return if (totalTasks == 0) 0 else (completedTasks * 100) / totalTasks
    }
}