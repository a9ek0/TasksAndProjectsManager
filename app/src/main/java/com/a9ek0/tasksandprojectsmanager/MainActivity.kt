package com.a9ek0.tasksandprojectsmanager

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var calendarDays: List<CalendarDay>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calendarRecyclerView = findViewById(R.id.calendar_recycler_view)
        calendarRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        calendarDays = generateCalendarDays()
        calendarAdapter = CalendarAdapter(this, calendarDays, object : CalendarAdapter.OnDayClickListener {
            override fun onDayClick(day: CalendarDay) {
                Toast.makeText(this@MainActivity, "Selected day: ${day.dayNumber}", Toast.LENGTH_SHORT).show()
            }
        })

        calendarRecyclerView.adapter = calendarAdapter
    }

    private fun generateCalendarDays(): List<CalendarDay> {
        val days = mutableListOf<CalendarDay>()
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        calendar.set(year, month, 1)

        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in 1..daysInMonth) {
            calendar.set(year, month, i)
            val dayName = java.text.SimpleDateFormat("EEE").format(calendar.time)
            days.add(CalendarDay(i, dayName))
        }

        return days
    }
}

