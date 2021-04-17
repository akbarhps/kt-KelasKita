package com.charuniverse.kelasku.ui.main.dev_tools.edit_user

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.data.models.User
import com.charuniverse.kelasku.ui.main.MainActivity
import com.charuniverse.kelasku.util.helper.SnackBarBuilder
import kotlinx.android.synthetic.main.fragment_edit_user.*

class EditUserFragment : Fragment(R.layout.fragment_edit_user),
    EditUserAdapter.EditUserEvents,
    EditUserDialog.EditUserDialogEvents {

    private lateinit var viewModel: EditUserViewModel

    private lateinit var snackBar: SnackBarBuilder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideNavigationBar()

        snackBar = SnackBarBuilder(view)
        viewModel = ViewModelProvider(requireActivity())
            .get(EditUserViewModel::class.java)

        uiEventsListener()
        usersListener()
    }

    private fun usersListener() {
        viewModel.users.observe(viewLifecycleOwner, {
            recyclerEditUser.layoutManager = LinearLayoutManager(requireContext())
            recyclerEditUser.adapter = EditUserAdapter(this, it)
        })
    }

    private fun uiEventsListener() {
        viewModel.events.observe(viewLifecycleOwner, {
            when (it) {
                is EditUserViewModel.UIEvents.Idle ->
                    toggleProgressBar(false)
                is EditUserViewModel.UIEvents.Loading ->
                    toggleProgressBar(true)
                is EditUserViewModel.UIEvents.Success -> {
                    snackBar.short("Update sukses")
                    viewModel.setEventToIdle()
                }
                is EditUserViewModel.UIEvents.Error -> {
                    snackBar.short(it.message)
                    viewModel.setEventToIdle()
                }
            }
        })
    }

    private fun hideNavigationBar() {
        (activity as MainActivity).toggleNavigationBar(false)
    }

    private fun toggleProgressBar(show: Boolean) {
        viewEditProfileProgressBar.visibility = if (show) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun onUserClick(user: User) {
        EditUserDialog(user, this)
            .show(parentFragmentManager, null)
    }

    override fun onMenuEditClick(user: User, isAdmin: Boolean) {
        viewModel.updateUserPermission(user.email, isAdmin)
    }
}