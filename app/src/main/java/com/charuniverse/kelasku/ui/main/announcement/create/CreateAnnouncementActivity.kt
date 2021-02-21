package com.charuniverse.kelasku.ui.main.announcement.create

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.data.models.Announcement
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_create_announcement.*

class CreateAnnouncementActivity : AppCompatActivity() {

    private val viewModel: CreateAnnouncementViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_announcement)

        eventsListener()
        buttonClickListener()
    }

    private fun eventsListener() {
        viewModel.events.observe(this, {
            when (it) {
                is CreateAnnouncementViewModel.UIEvents.Loading -> {
                    toggleProgressBar(true)
                }
                is CreateAnnouncementViewModel.UIEvents.Success -> {
                    finish()
                }
                is CreateAnnouncementViewModel.UIEvents.Error -> {
                    buildSnackBar(it.error)
                    toggleProgressBar(false)
                }
                else -> Unit
            }
        })
    }

    private fun buttonClickListener() {
        announcementToolbar.setNavigationOnClickListener {
            finish()
        }
        cvCreateAnnouncement.setOnClickListener {
            val title = etAnnouncementTitle.text.toString()
            val body = etAnnouncementDesc.text.toString()
            val url = etCreateAnnouncementUrl.text.toString()
            viewModel.createAnnouncement(Announcement(title, body, url))
        }
    }

    private fun toggleProgressBar(show: Boolean) {
        if (show) {
            cvCreateAnnouncement.isEnabled = false
            llCreateAnnouncementText.visibility = View.GONE
            llCreateAnnouncementLoading.visibility = View.VISIBLE
        } else {
            cvCreateAnnouncement.isEnabled = true
            llCreateAnnouncementLoading.visibility = View.GONE
            llCreateAnnouncementText.visibility = View.VISIBLE
        }
    }

    private fun buildSnackBar(message: String) {
        val view = findViewById<ConstraintLayout>(R.id.clCreateAnnouncementRoot)
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }
}