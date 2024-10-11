package com.a9ek0.tasksandprojectsmanager

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HourAdapter(
    private val hours: List<String>,
    private val onTaskLongClickListener: OnTaskLongClickListener,
    private var projects: Map<Int, Project>
) : RecyclerView.Adapter<HourAdapter.HourViewHolder>() {

    interface OnTaskLongClickListener {
        fun onTaskLongClick(task: Task)
    }

    private val tasksByHour = mutableMapOf<Int, MutableList<Task>>()
    private var currentDate: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.hour_item, parent, false)
        return HourViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {
        val hour = hours[position]
        val tasks = tasksByHour[position] ?: emptyList()
        holder.bind(hour, tasks, projects, onTaskLongClickListener)
    }

    override fun getItemCount(): Int = hours.size

    fun addTaskToHour(hourIndex: Int, task: Task) {
        for (i in 0 until task.duration) {
            tasksByHour.getOrPut(hourIndex + i) { mutableListOf() }.add(task)
        }
        notifyDataSetChanged()
    }

    fun updateProjects(newProjects: Map<Int, Project>) {
        projects = newProjects
        notifyDataSetChanged()
    }

    fun clearTasks() {
        tasksByHour.clear()
        notifyDataSetChanged()
    }

    fun setCurrentDate(date: String) {
        currentDate = date
    }

    fun notifyTaskChanged(task: Task) {
        tasksByHour.forEach { (hourIndex, tasks) ->
            if (tasks.contains(task)) {
                notifyItemChanged(hourIndex)
            }
        }
    }

    class HourViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val hourTextView: TextView = itemView.findViewById(R.id.hour_text)
        private val tasksContainer: LinearLayout = itemView.findViewById(R.id.tasks_container)

        fun bind(hour: String, tasks: List<Task>, projects: Map<Int, Project>, onTaskLongClickListener: OnTaskLongClickListener) {
            hourTextView.text = hour
            tasksContainer.removeAllViews()
            tasks.forEach { task ->
                val taskView = LayoutInflater.from(itemView.context).inflate(R.layout.task_item, tasksContainer, false)
                val titleTextView: TextView = taskView.findViewById(R.id.task_title)
                val descriptionTextView: TextView = taskView.findViewById(R.id.task_description)
                val projectTextView: TextView = taskView.findViewById(R.id.task_project)
                titleTextView.text = task.title
                descriptionTextView.text = task.description
                projectTextView.text = projects[task.projectId]?.name ?: "No Project"

                // Set paint flags based on the completed status
                titleTextView.paintFlags = if (task.completed) {
                    titleTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    titleTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }

                taskView.setOnLongClickListener {
                    onTaskLongClickListener.onTaskLongClick(task)
                    true
                }

                taskView.setOnClickListener(object : View.OnClickListener {
                    private var lastClickTime: Long = 0

                    override fun onClick(v: View?) {
                        val clickTime = System.currentTimeMillis()
                        if (clickTime - lastClickTime < ViewConfiguration.getDoubleTapTimeout()) {
                            task.completed = !task.completed
                            titleTextView.paintFlags = if (task.completed) {
                                titleTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                            } else {
                                titleTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                            }
                            // Update task in database
                            CoroutineScope(Dispatchers.IO).launch {
                                AppDatabase.getDatabase(itemView.context).taskDao().update(task)
                            }
                            // Notify adapter to update all relevant views
                            (itemView.context as? TaskListActivity)?.onTaskUpdated(task)
                        }
                        lastClickTime = clickTime
                    }
                })

                tasksContainer.addView(taskView)
            }
        }
    }
}