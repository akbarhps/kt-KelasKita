package com.charuniverse.kelasku.ui.main.assignment.create

import android.annotation.SuppressLint
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
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.data.models.Assignment
import com.charuniverse.kelasku.ui.main.MainActivity
import com.charuniverse.kelasku.util.AppPreferences
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_assignment_create.*
import java.text.SimpleDateFormat
import java.util.*

class AssignmentCreateFragment : Fragment(R.layout.fragment_assignment_create),
    DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private val timePicked = GregorianCalendar(TimeZone.getTimeZone("GMT+7"))
    private var endTimestamp = 0L
    private lateinit var viewModel: AssignmentCreateViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toggleNavigationBar(false)

        viewModel = ViewModelProvider(requireActivity())
            .get(AssignmentCreateViewModel::class.java)

        uiManager()
        uiEventListener()
    }

    private fun uiEventListener() {
        viewModel.events.observe(viewLifecycleOwner, {
            when (it) {
                is AssignmentCreateViewModel.UIEvents.Idle -> toggleProgressBar(false)
                is AssignmentCreateViewModel.UIEvents.Loading -> toggleProgressBar(true)
                is AssignmentCreateViewModel.UIEvents.Success -> {
                    findNavController().navigateUp()
                    viewModel.setEventToIdle()
                }
                is AssignmentCreateViewModel.UIEvents.Error -> {
                    buildSnackBar(it.message)
                    viewModel.setEventToIdle()
                }
            }
        })
    }

    private fun uiManager() {
        cvCreateAssignment.setOnClickListener {
            hideKeyboard()
            val course = etCreateAssignmentCourse.text.toString()
            val title = etCreateAssignmentTitle.text.toString()
            val description = etCreateAssignmentDesc.text.toString()
            val url = etCreateAssignmentUrl.text.toString()

            viewModel.createAssignment(
                Assignment(
                    course, title, description, url,
                    AppPreferences.userEmail, endTimestamp
                )
            )
        }

        cvCreateAssignmentPickDate.setOnClickListener {
            Calendar.getInstance().let {
                val day = it.get(Calendar.DAY_OF_MONTH)
                val month = it.get(Calendar.MONTH)
                val year = it.get(Calendar.YEAR)
                DatePickerDialog(
                    requireContext(), this, year, month, day
                )
            }.apply {
                datePicker.minDate = System.currentTimeMillis() - 1000
                show()
            }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        timePicked.set(year, month, dayOfMonth)
        Calendar.getInstance().let {
            val currentHour = it.get(Calendar.HOUR)
            val currentMinute = it.get(Calendar.MINUTE)
            TimePickerDialog(
                requireContext(), this, currentHour, currentMinute, true
            )
        }.show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        timePicked.apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            endTimestamp = timeInMillis / 1000
        }
        tvCreateAssignmentDeadline.text = convertLongToDate(endTimestamp)
    }

    private fun toggleProgressBar(show: Boolean) {
        if (show) {
            cvCreateAssignment.isEnabled = false
            llCreateAssignmentText.visibility = View.GONE
            llCreateAssignmentProgress.visibility = View.VISIBLE
        } else {
            cvCreateAssignment.isEnabled = true
            llCreateAssignmentText.visibility = View.VISIBLE
            llCreateAssignmentProgress.visibility = View.GONE
        }
    }

    private fun buildSnackBar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertLongToDate(time: Long): String {
        val date = Date(time * 1000)
        return SimpleDateFormat("dd/MM/yyy HH:mm").format(date)
    }

    private fun toggleNavigationBar(show: Boolean) {
        (activity as MainActivity).toggleNavigationBar(show)
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
        toggleNavigationBar(true)
        super.onDetach()
    }
}