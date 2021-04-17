package com.charuniverse.kelasku.ui.main.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.ui.login.LoginActivity
import com.charuniverse.kelasku.ui.main.MainActivity
import com.charuniverse.kelasku.ui.main.dev_tools.DevToolsMenuDialog
import com.charuniverse.kelasku.util.AppPreferences
import com.charuniverse.kelasku.util.helper.SnackBarBuilder
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment(R.layout.fragment_profile),
    ProfileMenuDialog.ProfileDialogMenuEvents,
    DevToolsMenuDialog.DevToolsMenuEvents {

    private val user = AppPreferences.getUserInfo()
    private lateinit var viewModel: ProfileViewModel

    private lateinit var profileMenuDialog: ProfileMenuDialog
    private lateinit var devToolsDialog: DevToolsMenuDialog
    private lateinit var snackBar: SnackBarBuilder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        snackBar = SnackBarBuilder(view)
        viewModel = ViewModelProvider(requireActivity())
            .get(ProfileViewModel::class.java)

        uiEventsListener()
        uiHandler()
    }

    private fun uiEventsListener() {
        viewModel.events.observe(viewLifecycleOwner, {
            when (it) {
                is ProfileViewModel.UIEvents.Idle ->
                    toggleProgressBar(false)
                is ProfileViewModel.UIEvents.Loading ->
                    toggleProgressBar(true)
                is ProfileViewModel.UIEvents.Success -> {
                    uiHandler()
                    snackBar.short("Profile sudah di update")
                    viewModel.setEventToIdle()
                }
                is ProfileViewModel.UIEvents.Error -> {
                    snackBar.long(it.message)
                    viewModel.setEventToIdle()
                }
                is ProfileViewModel.UIEvents.Complete -> updateUI()
            }
        })
    }

    private fun uiHandler() {
        tvProfileEmail.text = user.email
        tvProfileClass.text = "🏛 ${user.className}"

        profileMenuDialog = ProfileMenuDialog(this)
        devToolsDialog = DevToolsMenuDialog(this)

        cvOpenProfileMenu.setOnClickListener {
            profileMenuDialog.show(parentFragmentManager, null)
        }

        if (!AppPreferences.isDeveloper) {
            return
        }

        cvOpenDevMenu.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                devToolsDialog.show(parentFragmentManager, null)
            }
        }
    }

    private fun updateUI() {
        requireActivity().apply {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun toggleProgressBar(show: Boolean) {
        profileProgressBar.visibility = if (show) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun onMenuRefreshClick() {
        viewModel.refreshUserInfo()
    }

    override fun onMenuLogOutClick() {
        viewModel.logUserOut()
    }

    override fun onMenuAddClassClick() {
        val direction = ProfileFragmentDirections
            .actionProfileFragmentToCreateClassFragment()
        findNavController().navigate(direction)
    }

    override fun onMenuEditUsersClick() {
        val direction = ProfileFragmentDirections
            .actionProfileFragmentToEditUserFragment()
        findNavController().navigate(direction)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).toggleNavigationBar(true)
    }
}