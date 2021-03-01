package com.charuniverse.kelasku.ui.main.assignment

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.data.models.Assignment
import com.charuniverse.kelasku.util.AppPreferences
import com.charuniverse.kelasku.util.Globals
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_assignment.*

class AssignmentFragment : Fragment(R.layout.fragment_assignment),
    AssignmentAdapter.AssignmentEvents {

    private lateinit var viewModel: AssignmentViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())
            .get(AssignmentViewModel::class.java)

        if (AppPreferences.isUserAdmin) {
            fabCreateAssignment.visibility = View.VISIBLE
        }

        eventsListener()
        uiManager()
    }

    private fun eventsListener() {
        viewModel.event.observe(viewLifecycleOwner, {
            val isLoading = when (it) {
                is AssignmentViewModel.UIEvents.Loading -> true
                is AssignmentViewModel.UIEvents.NoData -> {
                    toggleNoDataState(true)
                    false
                }
                is AssignmentViewModel.UIEvents.Error -> {
                    buildSnackBar(it.error)
                    false
                }
                else -> {
                    toggleNoDataState(false)
                    false
                }
            }
            toggleRefreshAnimation(isLoading)
        })
    }

    private fun uiManager() {
        viewModel.assignments.observe(viewLifecycleOwner, { assignments ->
            val assignmentAdapter = AssignmentAdapter(this, assignments)
            recyclerAssignment.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = assignmentAdapter
            }
        })

        srAssignment.setOnRefreshListener {
            viewModel.getAssignment()
        }

        fabCreateAssignment.setOnClickListener {
            findNavController().navigate(
                R.id.action_assignmentFragment_to_assignmentCreateFragment
            )
        }
    }

    private fun toggleNoDataState(show: Boolean) {
        viewNoAssignment.visibility = if (show) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun toggleRefreshAnimation(show: Boolean) {
        srAssignment.isRefreshing = show
    }

    private fun buildSnackBar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
            .setAction("Refresh") {
                viewModel.getAssignment()
            }.show()
    }

    override fun onItemClick(assignment: Assignment) {
        val bundle = bundleOf("assignment" to assignment)
        findNavController().navigate(
            R.id.action_assignmentFragment_to_assignmentDetailFragment, bundle
        )
    }

    override fun onResume() {
        super.onResume()
        if (Globals.refreshAssignment) {
            viewModel.getAssignment()
        }
    }
}