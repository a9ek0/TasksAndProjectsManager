package com.a9ek0.tasksandprojectsmanager

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val hoursContainer = findViewById<LinearLayout>(R.id.hours_container)

        for (hour in 0..23) {
            val hourView = layoutInflater.inflate(R.layout.hour_block, hoursContainer, false)
            val hourText = hourView.findViewById<TextView>(R.id.hour_text)

            val timeString = when {
                hour == 0 -> "12 AM"
                hour < 12 -> "$hour AM"
                hour == 12 -> "12 PM"
                else -> "${hour - 12} PM"
            }

            hourText.text = timeString
            hoursContainer.addView(hourView)
        }

    }
}
