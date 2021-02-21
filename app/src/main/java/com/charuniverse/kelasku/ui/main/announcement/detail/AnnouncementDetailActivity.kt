package com.charuniverse.kelasku.ui.main.announcement.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.data.models.Announcement
import com.charuniverse.kelasku.util.AppPreferences
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_announcement_detail.*
import java.text.SimpleDateFormat
import java.util.*

class AnnouncementDetailActivity : AppCompatActivity() {

    private var isLoading = false
    private val viewModel: AnnouncementDetailViewModel by viewModels()
    private var announcement: Announcement? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_announcement_detail)

        setSupportActionBar(announcementDetailToolbar)
        announcement = intent.getParcelableExtra("Announcement")

        setupUI()
        eventsListener()
        clickListener()
    }

    private fun setupUI() {
        announcement?.let {
            tvAnnouncementDetailDate.text = convertLongToDate(it.createTimestamp)
            tvAnnouncementDetailTitle.text = it.title
            tvAnnouncementDetailBody.text = it.body
            val creator = "Posted by : " + it.creator
            tvAnnouncementDetailCreator.text = creator

            if (it.url.isNotEmpty()) {
                cvAnnouncementDetailUrl.visibility = View.VISIBLE
            }
        }
    }

    private fun eventsListener() {
        viewModel.events.observe(this, {
            when (it) {
                is AnnouncementDetailViewModel.UIEvents.Loading -> {
                    isLoading = true
                }
                is AnnouncementDetailViewModel.UIEvents.Success -> {
                    isLoading = false
                    finish()
                }
                is AnnouncementDetailViewModel.UIEvents.Error -> {
                    isLoading = false
                    buildSnackBar(it.message)
                }
                else -> Unit
            }
            toggleProgressBar()
        })
    }

    private fun clickListener() {
        announcementDetailToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        cvAnnouncementDetailUrl.setOnClickListener {
            announcement?.let {
                val openURL = Intent(Intent.ACTION_VIEW)
                openURL.data = Uri.parse(it.url)
                startActivity(openURL)
            }
        }
        announcementDetailToolbar.setOnMenuItemClickListener {
            val rootView = findViewById<ConstraintLayout>(R.id.clAnnouncementDetailRoot)
            if (it.itemId == R.id.menu_delete) {
                Snackbar.make(rootView, "Apakah anda yakin?", Snackbar.LENGTH_LONG)
                    .setAction("Yakin") {
                        viewModel.deleteAnnouncement(announcement!!.id)
                    }.show()
            }
            true
        }
    }

    private fun toggleProgressBar() {
        if (isLoading) {
            cvAnnouncementDetailProgress.visibility = View.VISIBLE
        } else {
            cvAnnouncementDetailProgress.visibility = View.GONE
        }
    }

    private fun buildSnackBar(message: String) {
        val rootView = findViewById<ConstraintLayout>(R.id.clAnnouncementDetailRoot)
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (AppPreferences.isUserAdmin) {
            menuInflater.inflate(R.menu.toolbar_menu_delete, menu)
        }
        return true
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertLongToDate(time: Long): String {
        val date = Date(time * 1000)
        return SimpleDateFormat("E, dd/MM/yyy").format(date)
    }

    override fun onBackPressed() {
        if (!isLoading) {
            super.onBackPressed()
        }
    }
}