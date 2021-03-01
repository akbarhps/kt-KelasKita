package com.charuniverse.kelasku.ui.main.profile

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.ui.login.LoginActivity
import com.charuniverse.kelasku.util.AppPreferences
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private enum class SnackbarType {
        BASIC,
        LOG_OUT
    }

    private val user = AppPreferences.getUserInfo()
    private lateinit var viewModel: ProfileViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = ViewModelProvider(requireActivity())
            .get(ProfileViewModel::class.java)

        uiEventsListener()
        uiManager()
    }

    private fun uiEventsListener() {
        viewModel.events.observe(viewLifecycleOwner, {
            when (it) {
                is ProfileViewModel.UIEvents.Idle -> Unit
                is ProfileViewModel.UIEvents.Loading -> toggleProgressBar(true)
                is ProfileViewModel.UIEvents.Success -> {
                    toggleProgressBar(false)
                    buildSnackBar("Profile sudah di update")
                    viewModel.setEventToIdle()
                }
                is ProfileViewModel.UIEvents.Error -> {
                    toggleProgressBar(false)
                    buildSnackBar(it.message, SnackbarType.LOG_OUT)
                    viewModel.setEventToIdle()
                }
                is ProfileViewModel.UIEvents.LoggedOut -> updateUI()
            }
        })
    }

    private fun uiManager() {
        tvProfileEmail.text = user.email
        tvProfileClass.text = "🏛 ${user.className}"
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_profile, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_profile_refresh -> viewModel.refreshUserInfo()
            R.id.menu_profile_log_out -> buildSnackBar(
                "Apakah Anda yakin?",
                SnackbarType.LOG_OUT
            )
        }
        return super.onOptionsItemSelected(item)
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

    private fun buildSnackBar(message: String, type: SnackbarType = SnackbarType.BASIC) {
        val snackBar = Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT)
        when (type) {
            SnackbarType.BASIC -> Unit
            SnackbarType.LOG_OUT -> snackBar.setAction("Log Out") {
                viewModel.logUserOut()
            }
        }
        snackBar.show()
    }
}