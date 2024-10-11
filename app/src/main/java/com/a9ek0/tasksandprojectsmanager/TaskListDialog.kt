package com.a9ek0.tasksandprojectsmanager

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TaskListDialog(context: Context, private val tasks: List<Task>, private val title: String) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_list_dialog)

        // Set the dialog to full screen
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)

        val titleTextView: TextView = findViewById(R.id.dialog_title)
        titleTextView.text = title

        val recyclerView: RecyclerView = findViewById(R.id.task_list_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = TaskAdapter { task ->
            // Handle task swipe here
        }.apply {
            submitList(tasks)
        }
    }
}