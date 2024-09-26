package com.a9ek0.tasksandprojectsmanager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CalendarAdapter(
    private val context: Context,
    private val calendarDays: List<CalendarDay>,
    private val onDayClickListener: OnDayClickListener
) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    private var selectedDay: CalendarDay? = null

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
        holder.dayIcon.visibility = if (day == selectedDay) View.VISIBLE else View.GONE
        holder.itemView.setOnClickListener {
            selectedDay = day
            onDayClickListener.onDayClick(day)
            notifyDataSetChanged()
        }
    }



    override fun getItemCount(): Int {
        return calendarDays.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayNumber: TextView = itemView.findViewById(R.id.day_number)
        val dayName: TextView = itemView.findViewById(R.id.day_name)
        val dayIcon: ImageView = itemView.findViewById(R.id.day_icon)
    }
}
