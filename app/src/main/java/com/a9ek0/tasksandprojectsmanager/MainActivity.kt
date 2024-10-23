package com.a9ek0.tasksandprojectsmanager

import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), GestureDetector.OnGestureListener {

    private lateinit var taskDao: TaskDao
    private lateinit var projectDao: ProjectDao
    private lateinit var gestureDetector: GestureDetector
    private lateinit var auth: FirebaseAuth

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }

    external fun calculateProgressJNI(totalTasks: Int, completedTasks: Int): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportActionBar != null) {
            supportActionBar?.hide()
        }
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        val nameTextView: TextView = findViewById(R.id.name)
        nameTextView.text = user.displayName ?: "User"

        gestureDetector = GestureDetector(this, this)

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
        projectDao = AppDatabase.getDatabase(this).projectDao()

        val today = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val dateTextView: TextView = findViewById(R.id.date)
        val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        dateTextView.text = currentDate

        lifecycleScope.launch {
            val today = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
            val tasksCount = withContext(Dispatchers.IO) {
                taskDao.countTasksByDate(today)
            }
            val totalTasksCount = withContext(Dispatchers.IO) {
                taskDao.countAllTasks()
            }
            val totalProjectsCount = withContext(Dispatchers.IO) {
                projectDao.countAllProjects()
            }
            val completedTasksCount = withContext(Dispatchers.IO) {
                taskDao.countCompletedTasks()
            }

            val tasksCountTextView: TextView = findViewById(R.id.tasks_count)
            tasksCountTextView.text = tasksCount.toString()

            val totalTasksCountTextView: TextView = findViewById(R.id.total_tasks_count)
            totalTasksCountTextView.text = totalTasksCount.toString()

            val totalProjectsCountTextView: TextView = findViewById(R.id.total_projects_count)
            totalProjectsCountTextView.text = totalProjectsCount.toString()

            val progressBar: ProgressBar = findViewById(R.id.progress_bar)
            val progressTextView: TextView = findViewById(R.id.progress_text)
            val progressPercentage = calculateProgress(totalTasksCount, completedTasksCount)
            progressBar.progress = progressPercentage
            progressTextView.text = "$progressPercentage%"

            val tasksForToday = withContext(Dispatchers.IO) {
                taskDao.getTasksByDate(today)
            }
            val allTasks = withContext(Dispatchers.IO) {
                taskDao.getAllTasks()
            }
            val allProjects = withContext(Dispatchers.IO) {
                projectDao.getAllProjects()
            }
            val completedTasks = withContext(Dispatchers.IO) {
                taskDao.getCompletedTasks()
            }

            val tasksCard: CardView = findViewById(R.id.tasks_card)
            val completedTasksCard: CardView = findViewById(R.id.completed_tasks_card)
            val totalTasksCard: CardView = findViewById(R.id.total_tasks_card)
            val totalProjectsCard: CardView = findViewById(R.id.total_projects_card)

            tasksCard.setOnClickListener {
                TaskListDialog(this@MainActivity, tasksForToday, "Tasks for Today").show()
            }

            completedTasksCard.setOnClickListener {
                TaskListDialog(this@MainActivity, completedTasks, "Completed Tasks").show()
            }

            totalTasksCard.setOnClickListener {
                TaskListDialog(this@MainActivity, allTasks, "All Tasks").show()
            }

            totalProjectsCard.setOnClickListener {
                ProjectListDialog(this@MainActivity, allProjects, "All Projects").show()
            }
        }
    }

    private fun calculateProgress(totalTasks: Int, completedTasks: Int): Int {
        return calculateProgressJNI(totalTasks, completedTasks)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            gestureDetector.onTouchEvent(event)
        }
        return super.onTouchEvent(event)
    }

    override fun onDown(p0: MotionEvent): Boolean = true

    override fun onShowPress(p0: MotionEvent) {}

    override fun onSingleTapUp(p0: MotionEvent): Boolean = true

    override fun onScroll(
        e1: MotionEvent?,
        p1: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean = true

    override fun onLongPress(p0: MotionEvent) {}

    override fun onFling(
        e1: MotionEvent?,
        p1: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if (e1 != null && p1 != null) {
            val deltaX = p1.x - e1.x
            val deltaY = p1.y - e1.y
            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                if (deltaX > 0) {
                    // Swipe right
                    val intent = Intent(this, TaskListActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Swipe left
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                return true
            }
        }
        return false
    }
}