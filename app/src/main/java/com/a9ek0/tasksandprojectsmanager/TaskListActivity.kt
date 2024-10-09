package com.a9ek0.tasksandprojectsmanager

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class TaskListActivity : AppCompatActivity() {

    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var hourRecyclerView: RecyclerView
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var hourAdapter: HourAdapter
    private var selectedDate: String = ""
    private lateinit var calendarDays: List<CalendarDay>

    private lateinit var db: AppDatabase
    private lateinit var taskDao: TaskDao
    private lateinit var projectDao: ProjectDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getSupportActionBar() != null) {
            getSupportActionBar()?.hide();
        }
        setContentView(R.layout.activity_task_list)

        db = AppDatabase.getDatabase(this)
        taskDao = db.taskDao()
        projectDao = db.projectDao()

        setupCalendarView()
        setupHourView()

        val today = Calendar.getInstance()
        selectedDate = "${today.get(Calendar.DAY_OF_MONTH)}-${today.get(Calendar.MONTH) + 1}-${today.get(Calendar.YEAR)}"
        loadTasksForSelectedDate()

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

        val datePickerIcon: ImageView = findViewById(R.id.datePickerIcon)
        datePickerIcon.setOnClickListener {
            showDatePickerDialog()
        }
    }


    private fun setupProjectView() {
        val addProjectIcon: ImageView = findViewById(R.id.addProjectIcon)
        addProjectIcon.setOnClickListener {
            showAddProjectDialog()
        }
    }

    private fun showAddProjectDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Project")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val nameInput = EditText(this)
        nameInput.hint = "Project Name"
        layout.addView(nameInput)

        val descriptionInput = EditText(this)
        descriptionInput.hint = "Project Description"
        layout.addView(descriptionInput)

        builder.setView(layout)

        builder.setPositiveButton("Add") { dialog, which ->
            val name = nameInput.text.toString()
            val description = descriptionInput.text.toString()
            val project = Project(name = name, description = description)
            addProjectToDatabase(project)
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }

        builder.show()
    }

    private fun addProjectToDatabase(project: Project) {
        CoroutineScope(Dispatchers.IO).launch {
            projectDao.insert(project)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@TaskListActivity, "Project added", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            selectedDate = "${selectedDay}-${selectedMonth + 1}-${selectedYear}"
            Log.d(TAG, selectedDate)
            loadTasksForSelectedDate()
            updateCalendarView(selectedDay, selectedMonth, selectedYear)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun loadTasksForSelectedDate() {
        CoroutineScope(Dispatchers.IO).launch {
            val tasks = taskDao.getTasksByDateAndTime(selectedDate)
            withContext(Dispatchers.Main) {
                hourAdapter.clearTasks()
                updateUI(tasks)
            }
        }
    }

    private fun updateCalendarView(selectedDay: Int, selectedMonth: Int, selectedYear: Int) {
        calendarDays = generateCalendarDays(selectedMonth, selectedYear)
        calendarAdapter.updateCalendarDays(calendarDays)

        val todayIndex = calendarDays.indexOfFirst { it.dayNumber == selectedDay }
        if (todayIndex != -1) {
            calendarAdapter.selectedPosition = todayIndex
            calendarAdapter.notifyDataSetChanged()

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
                calendarRecyclerView.layoutManager?.startSmoothScroll(smoothScroller)
            }
        }
    }

    private fun generateCalendarDays(month: Int, year: Int): List<CalendarDay> {
        val days = mutableListOf<CalendarDay>()
        val calendar = Calendar.getInstance()
        calendar.set(year, month, 1)

        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in 1..daysInMonth) {
            calendar.set(year, month, i)
            val dayName = java.text.SimpleDateFormat("EEE").format(calendar.time)
            days.add(CalendarDay(i, dayName))
        }

        return days
    }

    private fun updateCalendarView(selectedDay: Int) {
        val todayIndex = calendarDays.indexOfFirst { it.dayNumber == selectedDay }
        if (todayIndex != -1) {
            calendarAdapter.selectedPosition = todayIndex
            calendarAdapter.notifyDataSetChanged()

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
                calendarRecyclerView.layoutManager?.startSmoothScroll(smoothScroller)
            }
        }
    }

    private fun clearDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            db.clearDatabase()
        }
    }



    private fun updateUI(tasks: List<Task>) {
        hourAdapter.setCurrentDate(selectedDate)
        tasks.forEach { task ->
            val hourIndex = task.time.split(":")[0].toInt()
            hourAdapter.addTaskToHour(hourIndex, task)
        }
    }

    private fun addTaskToDatabase(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.insert(task)
            val tasks = taskDao.getTasksByDateAndTime(task.date)
            withContext(Dispatchers.Main) {
                hourAdapter.clearTasks()
                updateUI(tasks)
            }
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
            val task = Task(title = title, description = description, date = selectedDate, time = "", duration = 1)
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
            val selectedHour = hours[which]
            val updatedTask = task.copy(time = selectedHour)
            selectDurationForTask(updatedTask)
        }
        builder.show()
    }

    private fun selectDurationForTask(task: Task) {
        val durations = (1..24).map { "$it hour(s)" }.toTypedArray()
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Duration")
        builder.setItems(durations) { dialog, which ->
            val selectedDuration = which + 1
            val updatedTask = task.copy(duration = selectedDuration)
            addTaskToDatabase(updatedTask)
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
                loadTasksForSelectedDate()
            }
        })

        val today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val todayIndex = calendarDays.indexOfFirst { it.dayNumber == today }
        if (todayIndex != -1) {
            calendarAdapter.selectedPosition = todayIndex
            calendarAdapter.notifyItemChanged(todayIndex)

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

        CoroutineScope(Dispatchers.IO).launch {
            val projectsList = projectDao.getAllProjects()
            val projectsMap = projectsList.associateBy { it.id }
            withContext(Dispatchers.Main) {
                hourAdapter = HourAdapter(hours, object : HourAdapter.OnTaskLongClickListener {
                    override fun onTaskLongClick(task: Task) {
                        showTaskOptionsDialog(task)
                    }
                }, projectsMap)
                hourRecyclerView.adapter = hourAdapter
                loadTasksForSelectedDate()
            }
        }
    }
    private fun showTaskOptionsDialog(task: Task) {
        val options = arrayOf("Add to Existing Project", "Create New Project")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Task Options")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> showAddToProjectDialog(task)
                1 -> showAddProjectDialog()
            }
        }
        builder.show()
    }

    private fun showAddToProjectDialog(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            val projects = projectDao.getAllProjects()
            withContext(Dispatchers.Main) {
                val projectNames = projects.map { it.name }.toTypedArray()
                val builder = AlertDialog.Builder(this@TaskListActivity)
                builder.setTitle("Select Project")
                builder.setItems(projectNames) { dialog, which ->
                    val selectedProject = projects[which]
                    addTaskToProject(task, selectedProject)
                }
                builder.show()
            }
        }
    }

    private fun addTaskToProject(task: Task, project: Project) {
        CoroutineScope(Dispatchers.IO).launch {
            val updatedTask = task.copy(projectId = project.id)
            taskDao.update(updatedTask)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@TaskListActivity, "Task added to project", Toast.LENGTH_SHORT).show()
                loadTasksForSelectedDate()
            }
        }
    }

}

