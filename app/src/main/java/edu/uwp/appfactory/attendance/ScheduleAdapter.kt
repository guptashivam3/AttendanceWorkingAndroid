package edu.uwp.appfactory.attendance

import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//typical recycler adapter class
class ScheduleAdapter(val scheduleList: Array<String>) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val scheduleTextView: TextView = itemView.findViewById(R.id.schedule_text)


        fun bind(className: String) {
            scheduleTextView.text = className
            scheduleTextView.setTypeface(null, Typeface.BOLD) // Set text to bold
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.schedule_item, parent, false) // Replace with your grid item layout XML

        val name = ScheduleViewHolder(view)
        name.scheduleTextView.setOnClickListener {
            val intent = Intent(parent.context, ScanQR::class.java)
            parent.context.startActivity(intent)
        }
        return name
    }


    override fun getItemCount(): Int {
        return scheduleList.size
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val className = scheduleList[position]
        holder.bind(className)
    }
}
