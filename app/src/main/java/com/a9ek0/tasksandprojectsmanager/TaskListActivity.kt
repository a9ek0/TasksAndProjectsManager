package com.a9ek0.tasksandprojectsmanager

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

class TaskListActivity : AppCompatActivity() {

    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var hourRecyclerView: RecyclerView
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var hourAdapter: HourAdapter
    private var selectedDate: String = ""
    private lateinit var calendarDays: List<CalendarDay>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        setupCalendarView()
        setupHourView()

        val today = Calendar.getInstance()
        selectedDate = "${today.get(Calendar.DAY_OF_MONTH)}-${today.get(Calendar.MONTH) + 1}-${today.get(Calendar.YEAR)}"
        hourAdapter.setCurrentDate(selectedDate)

        val addIcon: ImageView = findViewById(R.id.addIcon)
        addIcon.setOnClickListener {
            if (selectedDate.isNotEmpty()) {
                showAddTaskDialog()
            } else {
                Toast.makeText(this, "Please select a date first", Toast.LENGTH_SHORT).show()
            }
        }

        val backIcon: ImageView = findViewById(R.id.backIcon)
        backIcon.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }


    private fun showAddTaskDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Task")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val titleInput = EditText(this)
        titleInput.hint = "Title"
        layout.addView(titleInput)

        val descriptionInput = EditText(this)
        descriptionInput.hint = "Description"
        layout.addView(descriptionInput)

        builder.setView(layout)

        builder.setPositiveButton("Add") { dialog, which ->
            val title = titleInput.text.toString()
            val description = descriptionInput.text.toString()
            val task = Task(title, description, selectedDate)
            selectHourForTask(task)
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }

        builder.show()
    }

    private fun selectHourForTask(task: Task) {
        val hours = (0..23).map { String.format("%02d:00", it) }.toTypedArray()
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Hour")
        builder.setItems(hours) { dialog, which ->
            if (!hourAdapter.addTaskToHour(which, task)) {
                Toast.makeText(this, "Maximum tasks reached for this hour", Toast.LENGTH_SHORT).show()
            }
        }
        builder.show()
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

    private fun setupCalendarView() {
        calendarRecyclerView = findViewById(R.id.calendar_recycler_view)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        calendarRecyclerView.layoutManager = layoutManager
        calendarRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.right = resources.getDimensionPixelSize(R.dimen.calendar_item_spacing)
                outRect.left = resources.getDimensionPixelSize(R.dimen.calendar_item_spacing)
            }
        })

        calendarDays = generateCalendarDays()
        calendarAdapter = CalendarAdapter(this, calendarDays, object : CalendarAdapter.OnDayClickListener {
            override fun onDayClick(day: CalendarDay) {
                selectedDate = "${day.dayNumber}-${Calendar.getInstance().get(Calendar.MONTH) + 1}-${Calendar.getInstance().get(Calendar.YEAR)}"
                hourAdapter.setCurrentDate(selectedDate)
            //    Toast.makeText(this@TaskListActivity, "Selected day: ${day.dayNumber}", Toast.LENGTH_SHORT).show()
            }
        })

        // Automatically select today's date
        val today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val todayIndex = calendarDays.indexOfFirst { it.dayNumber == today }
        if (todayIndex != -1) {
            calendarAdapter.selectedPosition = todayIndex
            calendarAdapter.notifyItemChanged(todayIndex)

            // Center the selected day
            calendarRecyclerView.post {
                val smoothScroller = object : LinearSmoothScroller(this@TaskListActivity) {
                    override fun getHorizontalSnapPreference(): Int {
                        return SNAP_TO_START
                    }

                    override fun calculateDtToFit(
                        viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int
                    ): Int {
                        return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2)
                    }
                }
                smoothScroller.targetPosition = todayIndex
                layoutManager.startSmoothScroll(smoothScroller)
            }
        }

        calendarRecyclerView.adapter = calendarAdapter
    }

    private fun setupHourView() {
        hourRecyclerView = findViewById(R.id.hour_recycler_view)
        val layoutManager = LinearLayoutManager(this)
        hourRecyclerView.layoutManager = layoutManager

        val hours = listOf("00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00",
            "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00")
        hourAdapter = HourAdapter(hours)
        hourRecyclerView.adapter = hourAdapter
    }
}

