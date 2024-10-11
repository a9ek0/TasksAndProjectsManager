package com.a9ek0.tasksandprojectsmanager

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ProjectListDialog(
    context: Context,
    private val projects: List<Project>,
    private val title: String
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.project_list_dialog)

        // Set the dialog to full screen
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)

        val titleTextView: TextView = findViewById(R.id.dialog_title)
        titleTextView.text = title

        val recyclerView: RecyclerView = findViewById(R.id.project_list_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = ProjectAdapter(projects)
    }
}