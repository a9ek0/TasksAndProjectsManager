package com.a9ek0.tasksandprojectsmanager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

class CalendarAdapter(
    private val context: Context,
    private val calendarDays: List<CalendarDay>,
    public val onDayClickListener: OnDayClickListener
) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    public var selectedPosition: Int = -1

    interface OnDayClickListener {
        fun onDayClick(day: CalendarDay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.calendar_day_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val day = calendarDays[position]
        holder.dayNumber.text = day.dayNumber.toString()
        holder.dayName.text = day.dayName
        holder.dayIcon.visibility = if (position == selectedPosition) View.VISIBLE else View.GONE

        val translationY = if (position == selectedPosition) -20f else 20f
        holder.dayContent.animate()
            .translationY(-translationY + 20f)
            .setDuration(400)
            .start()

        holder.dayContainer.animate()
            .translationY(translationY)
            .setDuration(400)
            .start()

        holder.itemView.setOnClickListener {
            val previousSelected = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousSelected)
            notifyItemChanged(selectedPosition)
            onDayClickListener.onDayClick(day)
        }

        holder.itemView.setOnClickListener {
            val previousSelected = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousSelected)
            notifyItemChanged(selectedPosition)
            onDayClickListener.onDayClick(day)

            val layoutManager = (holder.itemView.parent as RecyclerView).layoutManager as LinearLayoutManager
            val smoothScroller = object : LinearSmoothScroller(context) {
                override fun getHorizontalSnapPreference(): Int {
                    return SNAP_TO_START
                }

                override fun calculateDtToFit(
                    viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int
                ): Int {
                    return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2)
                }
            }
            smoothScroller.targetPosition = selectedPosition
            layoutManager.startSmoothScroll(smoothScroller)
        }
    }



    override fun getItemCount(): Int {
        return calendarDays.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayContainer: LinearLayout = itemView.findViewById(R.id.day_container)
        val dayContent: LinearLayout = itemView.findViewById(R.id.day_content)
        val dayNumber: TextView = itemView.findViewById(R.id.day_number)
        val dayName: TextView = itemView.findViewById(R.id.day_name)
        val dayIcon: ImageView = itemView.findViewById(R.id.day_icon)
    }

}