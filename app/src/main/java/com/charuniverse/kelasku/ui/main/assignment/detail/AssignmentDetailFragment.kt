package com.charuniverse.kelasku.ui.main.assignment.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.data.models.Assignment
import com.charuniverse.kelasku.ui.main.MainActivity
import com.charuniverse.kelasku.util.AppPreferences
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_assignment_detail.*
import java.text.SimpleDateFormat
import java.util.*

class AssignmentDetailFragment : Fragment(R.layout.fragment_assignment_detail) {

    private var assignmentId: String? = null
    private val args: AssignmentDetailFragmentArgs by navArgs()
    private lateinit var viewModel: AssignmentDetailViewModel

    enum class SnackBarType {
        BASIC,
        IGNORE_ITEM,
        DELETE_ITEM,
        NETWORK_ERROR
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toggleNavigationBar(false)
        setHasOptionsMenu(true)

        viewModel = ViewModelProvider(requireActivity())
            .get(AssignmentDetailViewModel::class.java)
            .also { it.argsHandler(args) }

        uiEventsListener()
        assignmentListener()
    }

    private fun uiEventsListener() {
        viewModel.events.observe(viewLifecycleOwner, {
            when (it) {
                is AssignmentDetailViewModel.UIEvents.Idle -> toggleProgressBar(false)
                is AssignmentDetailViewModel.UIEvents.Loading -> toggleProgressBar(true)
                is AssignmentDetailViewModel.UIEvents.Success -> {
                    findNavController().navigateUp()
                    viewModel.setEventToIdle()
                }
                is AssignmentDetailViewModel.UIEvents.Error -> {
                    buildSnackBar(it.message, Snackbar.LENGTH_LONG)
                    viewModel.setEventToIdle()
                }
            }
        })
    }

    private fun assignmentListener() {
        viewModel.assignment.observe(viewLifecycleOwner, {
            assignmentId = it.id
            uiHandler(it)
        })
    }

    private fun uiHandler(assignment: Assignment) {
        var dateAndCreator = convertLongToDate(assignment.endTimestamp)

        if (assignment.creator.isNotEmpty()) {
            dateAndCreator += " oleh: ${assignment.creator}"
        }

        tvAssignmentDetailDate.text = dateAndCreator
        tvAssignmentDetailTitle.text = assignment.title
        tvAssignmentDetailCourse.text = assignment.course
        tvAssignmentDetailDesc.text = if (hasHTMLTag(assignment.description)) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                Html.fromHtml(assignment.description, Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml(assignment.description)
            }
        } else {
            assignment.description
        }

        if (assignment.url.isEmpty()) return

        cvAssignmentDetailUrl.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(assignment.url)))
            }
        }
    }

    private fun toggleProgressBar(isLoading: Boolean) {
        assignmentDetailProgressBar.visibility = if (isLoading) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_assignment_done -> buildSnackBar(type = SnackBarType.IGNORE_ITEM)
            R.id.menu_assignment_delete -> buildSnackBar(type = SnackBarType.DELETE_ITEM)
            R.id.menu_assignment_share -> {
                val intent = Intent().apply {
                    type = "text/plain"
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, viewModel.shareUrl)
                }
                startActivity(Intent.createChooser(intent, "Bagikan ke:"))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun buildSnackBar(
        message: String = "Apakah anda yakin?",
        length: Int = Snackbar.LENGTH_SHORT,
        type: SnackBarType = SnackBarType.BASIC,
    ) {
        var snackLength = length
        val snackBarType = if (message.contains(".")) {
            snackLength = Snackbar.LENGTH_INDEFINITE
            SnackBarType.NETWORK_ERROR
        } else {
            type
        }

        val snackBar = Snackbar.make(requireView(), message, snackLength)
        when (snackBarType) {
            SnackBarType.IGNORE_ITEM -> snackBar.setAction("Selesai") {
                viewModel.addToIgnoreList()
            }
            SnackBarType.DELETE_ITEM -> snackBar.setAction("Hapus") {
                viewModel.deleteAssignment()
            }
            SnackBarType.NETWORK_ERROR -> snackBar.setAction("Try Again") {
                viewModel.getAssignmentById()
            }
            else -> Unit
        }
        snackBar.show()
    }

    private fun hasHTMLTag(text: String): Boolean {
        return "<[^>]*>".toRegex().containsMatchIn(text)
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertLongToDate(time: Long): String {
        val date = Date(time * 1000)
        return "Deadline: " + SimpleDateFormat("dd/MM/yyy HH:mm").format(date)
    }

    private fun toggleNavigationBar(show: Boolean) {
        (activity as MainActivity).toggleNavigationBar(show)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (AppPreferences.isDeveloper || AppPreferences.isUserAdmin) {
            inflater.inflate(R.menu.toolbar_admin_menu_assignment, menu)
        } else {
            inflater.inflate(R.menu.toolbar_menu_assignment, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDetach() {
        super.onDetach()
        toggleNavigationBar(true)
    }
}