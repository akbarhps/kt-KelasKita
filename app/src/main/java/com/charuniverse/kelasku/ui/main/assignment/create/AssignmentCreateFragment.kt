package com.charuniverse.kelasku.ui.main.assignment.create

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.data.models.Assignment
import com.charuniverse.kelasku.ui.main.MainActivity
import com.charuniverse.kelasku.util.AppPreferences
import com.charuniverse.kelasku.util.Globals
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_assignment_create.*

class AssignmentCreateFragment : Fragment(R.layout.fragment_assignment_create) {

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
                is AssignmentCreateViewModel.UIEvents.Loading -> toggleProgressBar(true)
                is AssignmentCreateViewModel.UIEvents.Success -> if(Globals.refreshAssignment) {
                    findNavController().navigateUp()
                }
                is AssignmentCreateViewModel.UIEvents.Error -> {
                    buildSnackBar(it.message)
                    toggleProgressBar(false)
                }
                else -> Unit
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
                    AppPreferences.userEmail
                )
            )
        }
    }

    private fun toggleProgressBar(show: Boolean) {
        if (show) {
            cvCreateAssignment.isEnabled = false
            tvCreateAssignmentText.visibility = View.GONE
            llCreateAssignmentProgress.visibility = View.VISIBLE
        } else {
            cvCreateAssignment.isEnabled = true
            tvCreateAssignmentText.visibility = View.VISIBLE
            llCreateAssignmentProgress.visibility = View.GONE
        }
    }

    private fun buildSnackBar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
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