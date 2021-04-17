package com.charuniverse.kelasku.ui.main.assignment.create

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.data.models.Assignment
import com.charuniverse.kelasku.ui.main.MainActivity
import com.charuniverse.kelasku.util.AppPreferences
import com.charuniverse.kelasku.util.helper.Converter.convertLongToDate
import com.charuniverse.kelasku.util.helper.PickerDialog
import com.charuniverse.kelasku.util.helper.SnackBarBuilder
import kotlinx.android.synthetic.main.fragment_assignment_create.*
import java.util.*

class AssignmentCreateFragment : Fragment(R.layout.fragment_assignment_create),
    DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    companion object {
        private lateinit var datePickerDialog: DatePickerDialog
        private lateinit var timePickerDialog: TimePickerDialog
    }

    private val args: AssignmentCreateFragmentArgs by navArgs()
    private lateinit var viewModel: AssignmentCreateViewModel

    private var endTimestamp = 0L
    private lateinit var snackBar: SnackBarBuilder
    private val timePicked = GregorianCalendar(TimeZone.getTimeZone("GMT+7"))

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideNavigationBar()

        snackBar = SnackBarBuilder(view)
        viewModel = ViewModelProvider(requireActivity())
            .get(AssignmentCreateViewModel::class.java)

        uiEventListener()
        uiHandler(args.assignment)
    }

    private fun uiEventListener() {
        viewModel.events.observe(viewLifecycleOwner, {
            when (it) {
                is AssignmentCreateViewModel.UIEvents.Idle ->
                    toggleProgressBar(false)
                is AssignmentCreateViewModel.UIEvents.Loading ->
                    toggleProgressBar(true)
                is AssignmentCreateViewModel.UIEvents.Complete -> {
                    findNavController().navigateUp()
                    viewModel.setEventToIdle()
                }
                is AssignmentCreateViewModel.UIEvents.Error -> {
                    showError(it.message)
                    viewModel.setEventToIdle()
                }
            }
        })
    }

    private fun showError(error: String) {
        val dateError = error.contains("tanggal")
        val courseError = error.contains("matakuliah")
        val titleError = error.contains("judul")
        val descError = error.contains("deskripsi")
        val urlError = error.contains("url")

        if (dateError) {
            tvCreateAssignmentDeadline.text = error
        } else if (courseError) {
            tilCreateAssignmentCourse.isErrorEnabled = true
            tilCreateAssignmentCourse.error = error
        } else if (titleError) {
            tilCreateAssignmentTitle.isErrorEnabled = true
            tilCreateAssignmentTitle.error = error
        } else if (descError) {
            tilCreateAssignmentDesc.isErrorEnabled = true
            tilCreateAssignmentDesc.error = error
        } else if (urlError) {
            tilCreateAssignmentUrl.isErrorEnabled = true
            tilCreateAssignmentUrl.error = error
        } else {
            snackBar.short(error)
        }
    }

    private fun hideError() {
        tilCreateAssignmentCourse.isErrorEnabled = false
        tilCreateAssignmentCourse.error = ""
        tilCreateAssignmentTitle.isErrorEnabled = false
        tilCreateAssignmentTitle.error = ""
        tilCreateAssignmentDesc.isErrorEnabled = false
        tilCreateAssignmentDesc.error = ""
        tilCreateAssignmentUrl.isErrorEnabled = false
        tilCreateAssignmentUrl.error = ""
    }

    private fun uiHandler(assignment: Assignment?) {
        val newAssignment = assignment ?: Assignment()

        assignment?.apply {
            etCreateAssignmentUrl.setText(url)
            etCreateAssignmentTitle.setText(title)
            etCreateAssignmentCourse.setText(course)
            etCreateAssignmentDesc.setText(description)
            this@AssignmentCreateFragment.endTimestamp = endTimestamp
            tvCreateAssignmentDeadline.text = "Dikumpul: \n" + convertLongToDate(endTimestamp)
        }

        cvCreateAssignmentPickDate.setOnClickListener {
            datePickerDialog = PickerDialog.buildDatePickerDialog(requireContext(), this)
            datePickerDialog.show()
        }

        cvCreateAssignment.setOnClickListener {
            hideKeyboard()
            hideError()

            val course = etCreateAssignmentCourse.text.toString()
            val title = etCreateAssignmentTitle.text.toString()
            val description = etCreateAssignmentDesc.text.toString()
            val url = etCreateAssignmentUrl.text.toString()
            val userEmail = AppPreferences.userEmail

            newAssignment.let {
                it.course = course
                it.title = title
                it.description = description
                it.url = url
                it.endTimestamp = endTimestamp
            }

            val action = if (assignment == null) {
                newAssignment.createdBy = userEmail
                AssignmentCreateViewModel.Action.ADD
            } else {
                newAssignment.editedBy = userEmail
                AssignmentCreateViewModel.Action.EDIT
            }

            viewModel.checkAssignment(newAssignment, action)
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        timePicked.set(year, month, dayOfMonth)
        timePickerDialog = PickerDialog.buildTimePickerDialog(requireContext(), this)
        timePickerDialog.show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        timePicked.apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            endTimestamp = timeInMillis / 1000
        }
        tvCreateAssignmentDeadline.text = "Dikumpul: \n" + convertLongToDate(endTimestamp)
    }

    private fun toggleProgressBar(show: Boolean) {
        if (show) {
            cvCreateAssignment.isEnabled = false
            llAssignmentCreateProgress.visibility = View.VISIBLE
        } else {
            cvCreateAssignment.isEnabled = true
            llAssignmentCreateProgress.visibility = View.GONE
        }
    }

    private fun hideNavigationBar() {
        (activity as MainActivity).toggleNavigationBar(false)
    }

    private fun hideKeyboard() {
        requireActivity().hideKeyboard()
    }

    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(requireContext()))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDetach() {
        hideKeyboard()
        super.onDetach()
    }
}