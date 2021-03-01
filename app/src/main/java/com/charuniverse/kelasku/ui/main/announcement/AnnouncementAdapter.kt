package com.charuniverse.kelasku.ui.main.announcement

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.data.models.Announcement
import kotlinx.android.synthetic.main.recycler_announcement.view.*
import java.text.SimpleDateFormat
import java.util.*

class AnnouncementAdapter(
    private val events: AnnouncementEvents,
    private val announcements: List<Announcement>
) : RecyclerView.Adapter<AnnouncementAdapter.ViewHolder>() {

    interface AnnouncementEvents {
        fun onItemClick(announcement: Announcement)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_announcement, parent, false)
        )
    }

    override fun getItemCount(): Int = announcements.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val announcement = announcements[position]

        holder.itemView.apply {
            tvAnnouncementListDate.text = convertLongToDate(announcement.createTimestamp)
            tvAnnouncementListTitle.text = announcement.title
            setOnClickListener { events.onItemClick(announcement) }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    @SuppressLint("SimpleDateFormat")
    private fun convertLongToDate(time: Long): String {
        val date = Date(time * 1000)
        return SimpleDateFormat("dd/MM/yyy").format(date)
    }
}