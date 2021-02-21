package com.charuniverse.kelasku.ui.main.assignment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.data.models.Assignment
import com.charuniverse.kelasku.ui.main.assignment.detail.AssignmentDetailActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.recycler_assignment.view.*
import java.text.SimpleDateFormat
import java.util.*

class AssignmentAdapter(
    private val activity: Activity,
    private val assignments: List<Assignment>
) : RecyclerView.Adapter<AssignmentAdapter.ViewHolder>() {

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
            it.tvAssignmentListDate.text = convertLongToDate(assignment.createTimestamp)
            it.tvAssignmentListTitle.text = assignment.title

            it.setOnClickListener {
                updateUI(assignment)
            }
        }
    }

    private fun updateUI(assignment: Assignment) {
        val intent = Intent(activity, AssignmentDetailActivity::class.java)
        intent.putExtra("Assignment", assignment)
        activity.startActivity(intent)
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertLongToDate(time: Long): String {
        val date = Date(time * 1000)
        return SimpleDateFormat("E, dd/MM/yyy").format(date)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}