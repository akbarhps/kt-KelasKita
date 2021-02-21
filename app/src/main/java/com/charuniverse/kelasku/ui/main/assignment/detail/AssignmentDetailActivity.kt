package com.charuniverse.kelasku.ui.main.assignment.detail

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
import com.charuniverse.kelasku.data.models.Assignment
import com.charuniverse.kelasku.util.AppPreferences
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_assignment_detail.*
import java.text.SimpleDateFormat
import java.util.*

class AssignmentDetailActivity : AppCompatActivity() {

    private var isLoading = false
    private var assignment: Assignment? = null
    private val viewModel: AssignmentDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assignment_detail)

        setSupportActionBar(assignmentDetailToolbar)
        assignment = intent.getParcelableExtra("Assignment")

        setupUI()
        eventsListener()
        uiListener()
    }

    private fun setupUI() {
        assignment?.let {
            var date = convertLongToDate(it.createTimestamp)
            if (it.creator.isNotEmpty()) {
                date += " by: ${it.creator}"
            }
            tvAssignmentDetailDate.text = date
            tvAssignmentDetailTitle.text = it.title
            tvAssignmentDetailCourse.text = it.course
            tvAssignmentDetailDesc.text = replaceAllHTMLTags(it.description)

            if (it.url.isNotEmpty()) {
                cvAssignmentDetailUrl.visibility = View.VISIBLE
            }
        }
    }

    private fun eventsListener() {
        viewModel.events.observe(this, {
            when (it) {
                is AssignmentDetailViewModel.UIEvents.Loading -> {
                    isLoading = true
                }
                is AssignmentDetailViewModel.UIEvents.Success -> {
                    isLoading = false
                    finish()
                }
                is AssignmentDetailViewModel.UIEvents.Error -> {
                    isLoading = false
                    buildSnackBar(it.message)
                }
                else -> Unit
            }
            toggleProgressBar()
        })
    }

    private fun uiListener() {
        assignmentDetailToolbar.setNavigationOnClickListener {
            if (!isLoading) {
                finish()
            }
        }
        cvAssignmentDetailUrl.setOnClickListener {
            assignment?.let {
                val openURL = Intent(Intent.ACTION_VIEW)
                openURL.data = Uri.parse(it.url)
                startActivity(openURL)
            }
        }
        assignmentDetailToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_done -> {
                    showAddIgnoreListSnackBar()
                }
                R.id.menu_delete -> {
                    showDeleteSnackBar()
                }
            }
            true
        }
    }

    private fun showAddIgnoreListSnackBar() {
        val rootView = findViewById<ConstraintLayout>(R.id.clAssignmentDetailRoot)
        Snackbar.make(rootView, "Apakah anda yakin?", Snackbar.LENGTH_LONG)
            .setAction("Yakin") {
                viewModel.addToIgnoreList(assignment!!.id)
            }.show()
    }

    private fun showDeleteSnackBar() {
        val rootView = findViewById<ConstraintLayout>(R.id.clAssignmentDetailRoot)
        Snackbar.make(rootView, "Apakah anda yakin?", Snackbar.LENGTH_LONG)
            .setAction("Yakin") {
                viewModel.deleteAssignment(assignment!!.id)
            }.show()
    }

    private fun toggleProgressBar() {
        if (isLoading) {
            cvAssignmentDetailProgress.visibility = View.VISIBLE
        } else {
            cvAssignmentDetailProgress.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (AppPreferences.isUserAdmin) {
            menuInflater.inflate(R.menu.toolbar_admin_menu, menu)
        } else {
            menuInflater.inflate(R.menu.toolbar_menu_done, menu)
        }
        return true
    }

    private fun buildSnackBar(message: String) {
        val rootView = findViewById<ConstraintLayout>(R.id.clAssignmentDetailRoot)
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertLongToDate(time: Long): String {
        val date = Date(time * 1000)
        return SimpleDateFormat("E, dd/MM/yyy").format(date)
    }

    private fun replaceAllHTMLTags(desc: String): String {
        val spaced = "<li>|<p>|(<br>|</br>|<br />)".toRegex().replace(desc, "\n")
        return deleteAllHtmlTag(spaced)
    }

    private fun deleteAllHtmlTag(desc: String): String {
        return "<[^>]*>".toRegex().replace(desc, "")
    }

    override fun onBackPressed() {
        if (!isLoading) {
            super.onBackPressed()
        }
    }
}