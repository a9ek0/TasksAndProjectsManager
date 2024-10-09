package com.a9ek0.tasksandprojectsmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HourAdapter(
    private val hours: List<String>,
    private val onTaskLongClickListener: OnTaskLongClickListener,
    private val projects: Map<Int, Project> // Добавьте карту проектов
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
        holder.bind(hour, tasks, projects)
        holder.itemView.setOnLongClickListener {
            if (tasks.isNotEmpty()) {
                onTaskLongClickListener.onTaskLongClick(tasks[0]) // Assuming you want to handle the first task
            }
            true
        }
    }

    override fun getItemCount(): Int = hours.size

    fun addTaskToHour(hourIndex: Int, task: Task) {
        for (i in 0 until task.duration) {
            tasksByHour.getOrPut(hourIndex + i) { mutableListOf() }.add(task)
        }
        notifyDataSetChanged()
    }

    fun clearTasks() {
        tasksByHour.clear()
        notifyDataSetChanged()
    }

    fun setCurrentDate(date: String) {
        currentDate = date
    }

    class HourViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val hourTextView: TextView = itemView.findViewById(R.id.hour_text)
        private val tasksContainer: LinearLayout = itemView.findViewById(R.id.tasks_container)

        fun bind(hour: String, tasks: List<Task>, projects: Map<Int, Project>) {
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
                tasksContainer.addView(taskView)
            }
        }
    }
}