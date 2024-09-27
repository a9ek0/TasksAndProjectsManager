package com.a9ek0.tasksandprojectsmanager

import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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