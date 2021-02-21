package com.charuniverse.kelasku.ui.main.assignment.create

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.data.models.Assignment
import com.charuniverse.kelasku.util.AppPreferences
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_create_assignment.*

class CreateAssignmentActivity : AppCompatActivity() {

    private val viewModel: CreateAssignmentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_assignment)

        eventsListener()
        buttonClickListener()
    }

    private fun eventsListener() {
        viewModel.events.observe(this, {
            when (it) {
                is CreateAssignmentViewModel.UIEvents.Loading -> {
                    toggleProgressBar(true)
                }
                is CreateAssignmentViewModel.UIEvents.Success -> {
                    finish()
                }
                is CreateAssignmentViewModel.UIEvents.Error -> {
                    buildSnackBar(it.message)
                    toggleProgressBar(false)
                }
                else -> Unit
            }
        })
    }

    private fun buttonClickListener() {
        assignmentToolbar.setNavigationOnClickListener {
            finish()
        }
        cvCreateAssignment.setOnClickListener {
            val course = etCreateAssignmentCourse.text.toString()
            val title = etCreateAssignmentTitle.text.toString()
            val description = etCreateAssignmentDesc.text.toString()
            val url = etCreateAssignmentUrl.text.toString()
            viewModel.createAssignment(
                Assignment(
                    course, title, description, url,
                    AppPreferences.userEmail
                )
            )
        }
    }

    private fun toggleProgressBar(show: Boolean) {
        if (show) {
            cvCreateAssignment.isEnabled = false
            llCreateAssignmentText.visibility = View.GONE
            llCreateAssignmentLoading.visibility = View.VISIBLE
        } else {
            cvCreateAssignment.isEnabled = true
            llCreateAssignmentLoading.visibility = View.GONE
            llCreateAssignmentText.visibility = View.VISIBLE
        }
    }

    private fun buildSnackBar(message: String) {
        val view = findViewById<ConstraintLayout>(R.id.clCreateAssignmentRoot)
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

}