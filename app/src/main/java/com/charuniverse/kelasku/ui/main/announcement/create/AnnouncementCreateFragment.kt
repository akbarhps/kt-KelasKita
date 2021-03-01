package com.charuniverse.kelasku.ui.main.announcement.create

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.data.models.Announcement
import com.charuniverse.kelasku.ui.main.MainActivity
import com.charuniverse.kelasku.util.AppPreferences
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_announcement_create.*

class AnnouncementCreateFragment : Fragment(R.layout.fragment_announcement_create) {

    private lateinit var viewModel: AnnouncementCreateViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toggleNavigationBar(false)

        viewModel = ViewModelProvider(requireActivity())
            .get(AnnouncementCreateViewModel::class.java)

        uiManager()
        uiEventListener()
    }

    private fun uiEventListener() {
        viewModel.events.observe(viewLifecycleOwner, {
            when (it) {
                is AnnouncementCreateViewModel.UIEvents.Idle -> toggleProgressBar(false)
                is AnnouncementCreateViewModel.UIEvents.Loading -> toggleProgressBar(true)
                is AnnouncementCreateViewModel.UIEvents.Success -> {
                    findNavController().navigateUp()
                    viewModel.setEventToIdle()
                }
                is AnnouncementCreateViewModel.UIEvents.Error -> {
                    buildSnackBar(it.error)
                    viewModel.setEventToIdle()
                }
            }
        })
    }

    private fun uiManager() {
        if (AppPreferences.isDeveloper) {
            cbAnnouncementToAll.visibility = View.VISIBLE
        }

        cvCreateAnnouncement.setOnClickListener {
            hideKeyboard()
            val title = etAnnouncementTitle.text.toString()
            val body = etAnnouncementDesc.text.toString()
            val url = etCreateAnnouncementUrl.text.toString()

            val classCode = if (cbAnnouncementToAll.isChecked) {
                "All"
            } else {
                AppPreferences.userClassCode
            }

            viewModel.createAnnouncement(Announcement(title, body, url, classCode))
        }
    }

    private fun toggleProgressBar(show: Boolean) {
        if (show) {
            cvCreateAnnouncement.isEnabled = false
            llCreateAnnouncementText.visibility = View.GONE
            llCreateAnnouncementProgress.visibility = View.VISIBLE
        } else {
            cvCreateAnnouncement.isEnabled = true
            llCreateAnnouncementText.visibility = View.VISIBLE
            llCreateAnnouncementProgress.visibility = View.GONE
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