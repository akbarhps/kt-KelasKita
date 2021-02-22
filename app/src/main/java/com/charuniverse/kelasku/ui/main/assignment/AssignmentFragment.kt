package com.charuniverse.kelasku.ui.main.assignment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.ui.main.assignment.create.CreateAssignmentActivity
import com.charuniverse.kelasku.util.AppPreferences
import kotlinx.android.synthetic.main.fragment_assignment.*

class AssignmentFragment : Fragment(R.layout.fragment_assignment) {

    private lateinit var baseView: View
    private lateinit var viewModel: AssignmentViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        baseView = view
        viewModel = ViewModelProvider(requireActivity())
            .get(AssignmentViewModel::class.java)

        if (AppPreferences.isUserAdmin) {
            fabCreateAssignment.visibility = View.VISIBLE
        }

        uiListener()
        eventsListener()
        assignmentListener()
    }

    private fun uiListener() {
        srAssignment.setOnRefreshListener {
            viewModel.getAssignment()
        }
        fabCreateAssignment.setOnClickListener {
            requireActivity()
                .startActivity(Intent(requireContext(), CreateAssignmentActivity::class.java))
        }
    }

    private fun eventsListener() {
        viewModel.event.observe(viewLifecycleOwner, {
            when (it) {
                is AssignmentViewModel.UIEvents.Loading -> toggleRefreshAnimation(true)
                is AssignmentViewModel.UIEvents.NoData -> {
                    toggleNoDataState(true)
                    toggleRefreshAnimation(false)
                }
                is AssignmentViewModel.UIEvents.Error -> {
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                    toggleRefreshAnimation(false)
                }
                else -> {
                    toggleNoDataState(false)
                    toggleRefreshAnimation(false)
                }
            }
        })
    }

    private fun assignmentListener() {
        viewModel.assignments.observe(viewLifecycleOwner, { assignments ->
            recyclerAssignment.let {
                it.layoutManager = LinearLayoutManager(requireContext())
                it.adapter = AssignmentAdapter(requireActivity(), assignments)
            }
        })
    }

    private fun toggleNoDataState(show: Boolean) {
        svNoAssignmentState.visibility = if (show) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun toggleRefreshAnimation(show: Boolean) {
        srAssignment.isRefreshing = show
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAssignment()
    }
}