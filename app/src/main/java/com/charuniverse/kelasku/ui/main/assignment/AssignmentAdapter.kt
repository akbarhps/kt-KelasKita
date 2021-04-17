package com.charuniverse.kelasku.ui.main.assignment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.data.models.Assignment
import kotlinx.android.synthetic.main.recycler_assignment.view.*
import java.text.SimpleDateFormat
import java.util.*

class AssignmentAdapter(
    private val events: AssignmentEvents,
    private val assignments: List<Assignment>
) : RecyclerView.Adapter<AssignmentAdapter.ViewHolder>() {

    interface AssignmentEvents {
        fun onItemClick(assignment: Assignment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_assignment, parent, false)
        )
    }

    override fun getItemCount(): Int = assignments.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val assignment = assignments[position]
        holder.itemView.let {
            it.tvAssignmentListDate.text = "${assignment.course}, ${convertLongToDate(assignment.endTimestamp)}"
            it.tvAssignmentListTitle.text = assignment.title

            it.setOnClickListener {
                events.onItemClick(assignment)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertLongToDate(time: Long): String {
        val date = Date(time * 1000)
        return SimpleDateFormat("dd/MM/yyy HH:mm").format(date)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}