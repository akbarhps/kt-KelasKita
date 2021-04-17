package com.charuniverse.kelasku.ui.main.assignment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.data.models.Assignment
import com.charuniverse.kelasku.ui.main.MainActivity
import com.charuniverse.kelasku.util.AppPreferences
import com.charuniverse.kelasku.util.Globals
import com.charuniverse.kelasku.util.helper.SnackBarBuilder
import kotlinx.android.synthetic.main.fragment_assignment.*

class AssignmentFragment : Fragment(R.layout.fragment_assignment),
    AssignmentAdapter.AssignmentEvents {

    override fun onItemClick(assignment: Assignment) {
        val direction = AssignmentFragmentDirections
            .actionAssignmentFragmentToAssignmentDetailFragment(assignment)
        findNavController().navigate(direction)
    }

    private lateinit var viewModel: AssignmentViewModel

    private lateinit var snackBar: SnackBarBuilder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        snackBar = SnackBarBuilder(view)
        viewModel = ViewModelProvider(requireActivity())
            .get(AssignmentViewModel::class.java)

        uiEventsListener()
        assignmentListener()
        uiHandler()
    }

    private fun uiEventsListener() {
        viewModel.event.observe(viewLifecycleOwner, {
            when (it) {
                is AssignmentViewModel.UIEvents.Idle -> {
                    toggleProgressBar(false)
                    toggleNoDataState(false)
                }
                is AssignmentViewModel.UIEvents.Loading -> {
                    toggleProgressBar(true)
                }
                is AssignmentViewModel.UIEvents.NoData -> {
                    toggleProgressBar(false)
                    toggleNoDataState(true)
                }
                is AssignmentViewModel.UIEvents.Error -> {
                    snackBar.actionShort(it.message, "Try Again") {
                        viewModel.getAssignment()
                    }
                    viewModel.setEventToIdle()
                }
            }
        })
    }

    private fun assignmentListener() {
        viewModel.assignments.observe(viewLifecycleOwner, { assignments ->
            val assignmentAdapter = AssignmentAdapter(this, assignments)
            recyclerAssignment.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = assignmentAdapter
            }
        })
    }

    private fun uiHandler() {
        if (AppPreferences.isUserAdmin) {
            cvAssignmentCreate.visibility = View.VISIBLE
        }

        srAssignment.setOnRefreshListener {
            viewModel.getAssignment()
        }

        cvAssignmentCreate.setOnClickListener {
            val direction = AssignmentFragmentDirections
                .actionAssignmentFragmentToAssignmentCreateFragment()
            findNavController().navigate(direction)
        }
    }

    private fun toggleNoDataState(show: Boolean) {
        viewNoAssignment.visibility = if (show) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun toggleProgressBar(show: Boolean) {
        srAssignment.isRefreshing = show
    }

    override fun onResume() {
        if (Globals.refreshAssignment) {
            viewModel.getAssignment()
        }
        (activity as MainActivity).toggleNavigationBar(true)
        super.onResume()
    }
}