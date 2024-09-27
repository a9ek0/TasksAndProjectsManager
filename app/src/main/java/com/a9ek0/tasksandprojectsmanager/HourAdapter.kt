package com.a9ek0.tasksandprojectsmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HourAdapter(private val hours: List<String>) : RecyclerView.Adapter<HourAdapter.ViewHolder>() {

    private val tasks: MutableList<MutableList<Task>> = MutableList(hours.size) { mutableListOf() }
    private val maxTasksPerHour = 5
    private var currentDate: String = ""

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val hourText: TextView = view.findViewById(R.id.hour_text)
        val taskContainer: LinearLayout = view.findViewById(R.id.task_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.hour_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.hourText.text = hours[position]
        holder.taskContainer.removeAllViews()
        for (task in tasks[position].filter { it.date == currentDate }) {
            val taskView = TextView(holder.taskContainer.context)
            taskView.text = "${task.title}: ${task.description}"
            taskView.setTextColor(holder.taskContainer.context.resources.getColor(R.color.black)) // Установите цвет текста
            holder.taskContainer.addView(taskView)
        }
    }

    override fun getItemCount() = hours.size

    fun getTasksForHour(hourIndex: Int): MutableList<Task> {
        return tasks[hourIndex]
    }

    fun addTaskToHour(hourIndex: Int, task: Task): Boolean {
        if (tasks[hourIndex].size < maxTasksPerHour) {
            tasks[hourIndex].add(task)
            notifyItemChanged(hourIndex)
            return true
        }
        return false
    }

    fun setCurrentDate(date: String) {
        currentDate = date
        notifyDataSetChanged()
    }
}