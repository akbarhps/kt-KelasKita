package com.charuniverse.kelasku.ui.main.announcement

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.data.models.Announcement
import com.charuniverse.kelasku.ui.main.announcement.detail.AnnouncementDetailActivity
import kotlinx.android.synthetic.main.recycler_announcement.view.*
import java.text.SimpleDateFormat
import java.util.*

class AnnouncementAdapter(
    private val activity: Activity,
    private val announcements: List<Announcement>
) : RecyclerView.Adapter<AnnouncementAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_announcement, parent, false)
        )
    }

    override fun getItemCount(): Int = announcements.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val announcement = announcements[position]
        holder.itemView.let {
            it.tvAnnouncementListDate.text = convertLongToDate(announcement.createTimestamp)
            it.tvAnnouncementListTitle.text = announcement.title

            it.setOnClickListener {
                updateUI(announcement)
            }
        }
    }

    private fun updateUI(announcement: Announcement) {
        val intent = Intent(activity, AnnouncementDetailActivity::class.java)
        intent.putExtra("Announcement", announcement)
        activity.startActivity(intent)
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertLongToDate(time: Long): String {
        val date = Date(time * 1000)
        return SimpleDateFormat("E, dd/MM/yyy").format(date)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}