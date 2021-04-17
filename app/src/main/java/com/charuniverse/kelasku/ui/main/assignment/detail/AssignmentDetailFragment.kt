package com.charuniverse.kelasku.ui.main.assignment.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.data.models.Assignment
import com.charuniverse.kelasku.ui.main.MainActivity
import com.charuniverse.kelasku.ui.main.dialog.ContentDetailMenuDialog
import com.charuniverse.kelasku.util.Globals
import com.charuniverse.kelasku.util.helper.Converter.convertDescription
import com.charuniverse.kelasku.util.helper.Converter.convertLongToDate
import com.charuniverse.kelasku.util.helper.ErrorStates
import kotlinx.android.synthetic.main.fragment_assignment_detail.*
import kotlinx.android.synthetic.main.view_network_error.*

class AssignmentDetailFragment : Fragment(R.layout.fragment_assignment_detail),
    ContentDetailMenuDialog.DetailMenuDialogEvents {

    private val args: AssignmentDetailFragmentArgs by navArgs()
    private lateinit var viewModel: AssignmentDetailViewModel

    private lateinit var menuDialogContent: ContentDetailMenuDialog
    private lateinit var editDestination: NavDirections

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideNavigationBar()

        viewModel = ViewModelProvider(requireActivity())
            .get(AssignmentDetailViewModel::class.java)
            .also { it.argsHandler(args) }

        uiEventsListener()
        assignmentListener()
    }

    private fun uiEventsListener() {
        viewModel.events.observe(viewLifecycleOwner, {
            when (it) {
                is AssignmentDetailViewModel.UIEvents.Idle -> {
                    hideAllStates()
                    toggleProgressBar(false)
                }
                is AssignmentDetailViewModel.UIEvents.Loading ->
                    toggleProgressBar(true)
                is AssignmentDetailViewModel.UIEvents.Complete ->
                    findNavController().navigateUp()
                is AssignmentDetailViewModel.UIEvents.Error -> {
                    hideAllStates()
                    toggleProgressBar(false)
                    when (it.state) {
                        ErrorStates.NOT_FOUND -> toggleNotFoundState(true)
                        ErrorStates.NO_ACCESS -> toggleNoAccessState(true)
                        ErrorStates.NETWORK_ERROR -> toggleNetworkErrorState(true)
                    }
                }
            }
        })
    }

    private fun assignmentListener() {
        viewModel.assignment.observe(viewLifecycleOwner, {
            assignmentHandler(it)

            menuDialogContent = ContentDetailMenuDialog(viewModel.permission, this)

            editDestination = AssignmentDetailFragmentDirections
                .actionAssignmentDetailFragmentToAssignmentCreateFragment(it)
        })

        cvNetworkErrorRetry.setOnClickListener {
            viewModel.getAssignmentById()
        }
    }

    private fun assignmentHandler(assignment: Assignment) {
        tvAssignmentDetailDate.text = "⏳\t" + convertLongToDate(assignment.endTimestamp)
        tvAssignmentDetailTitle.text = assignment.title
        tvAssignmentDetailCourse.text = assignment.course
        tvAssignmentDetailDesc.text = convertDescription(assignment.description)

        if (assignment.createdBy.isNotEmpty()) {
            tvAssignmentDetailPostedBy.visibility = View.VISIBLE
            tvAssignmentDetailPostedBy.text = "Oleh : ${assignment.createdBy}"
        }

        if (assignment.url.isNotEmpty()) {
            cvAssignmentDetailUrl.visibility = View.VISIBLE
        }

        cvAssignmentDetailUrl.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(assignment.url)))
        }

        cvAssignmentDetailMenu.visibility = View.VISIBLE
        cvAssignmentDetailMenu.setOnClickListener {
            menuDialogContent.show(parentFragmentManager, null)
        }
    }

    private fun toggleProgressBar(isLoading: Boolean) {
        if (isLoading) {
            cvNetworkErrorRetry.isEnabled = false
            viewProgressBar.visibility = View.VISIBLE
            llNetworkErrorRetryProgress.visibility = View.VISIBLE
        } else {
            cvNetworkErrorRetry.isEnabled = true
            viewProgressBar.visibility = View.GONE
            llNetworkErrorRetryProgress.visibility = View.GONE
        }
    }

    private fun hideAllStates() {
        toggleNotFoundState(false)
        toggleNoAccessState(false)
        toggleNetworkErrorState(false)
    }

    private fun toggleNotFoundState(show: Boolean) {
        viewNotFound.visibility = if (show) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun toggleNoAccessState(show: Boolean) {
        viewNoAccess.visibility = if (show) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun toggleNetworkErrorState(show: Boolean) {
        viewNetworkError.visibility = if (show) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun onMenuShareClick() {
        startActivity(Intent.createChooser(Intent().apply {
            type = "text/plain"
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, viewModel.shareUrl)
        }, "Bagikan ke : "))
    }

    override fun onMenuHideClick() {
        viewModel.addToIgnoreList()
    }

    override fun onMenuEditClick() {
        findNavController().navigate(editDestination)
    }

    override fun onMenuDeleteClick() {
        viewModel.deleteAssignment()
    }

    private fun hideNavigationBar() {
        (activity as MainActivity).toggleNavigationBar(false)
    }

    override fun onResume() {
        super.onResume()
        if (Globals.assignmentUpdated) {
            viewModel.getAssignmentById()
            Globals.assignmentUpdated = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.setEventToIdle()
    }
}