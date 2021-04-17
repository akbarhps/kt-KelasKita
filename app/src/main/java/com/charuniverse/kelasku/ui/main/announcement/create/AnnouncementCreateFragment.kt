package com.charuniverse.kelasku.ui.main.announcement.create

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.data.models.Announcement
import com.charuniverse.kelasku.ui.main.MainActivity
import com.charuniverse.kelasku.util.AppPreferences
import com.charuniverse.kelasku.util.helper.SnackBarBuilder
import kotlinx.android.synthetic.main.fragment_announcement_create.*

class AnnouncementCreateFragment : Fragment(R.layout.fragment_announcement_create) {

    private val args: AnnouncementCreateFragmentArgs by navArgs()
    private lateinit var viewModel: AnnouncementCreateViewModel

    private lateinit var snackBar: SnackBarBuilder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideNavigationBar()

        snackBar = SnackBarBuilder(view)
        viewModel = ViewModelProvider(requireActivity())
            .get(AnnouncementCreateViewModel::class.java)

        uiHandler(args.announcement)
        uiEventListener()
    }

    private fun uiHandler(announcement: Announcement?) {
        val newAnnouncement = announcement ?: Announcement()

        announcement?.apply {
            etCreateAnnouncementTitle.setText(title)
            etCreateAnnouncementDesc.setText(body)
            etCreateAnnouncementUrl.setText(url)

            if (classCode == "All") {
                cbAnnouncementToAll.isChecked = true
            }
        }

        if (AppPreferences.isDeveloper) {
            cbAnnouncementToAll.visibility = View.VISIBLE
        }

        cvCreateAnnouncement.setOnClickListener {
            hideKeyboard()
            hideError()

            val title = etCreateAnnouncementTitle.text.toString()
            val body = etCreateAnnouncementDesc.text.toString()
            val url = etCreateAnnouncementUrl.text.toString()

            val userEmail = AppPreferences.userEmail
            var classCode = AppPreferences.userClassCode
            if (cbAnnouncementToAll.isChecked) {
                classCode = "All"
            }

            newAnnouncement.let {
                it.url = url
                it.body = body
                it.title = title
                it.classCode = classCode
            }

            val action = if (announcement == null) {
                newAnnouncement.createdBy = userEmail
                AnnouncementCreateViewModel.Action.ADD
            } else {
                newAnnouncement.editedBy = userEmail
                AnnouncementCreateViewModel.Action.EDIT
            }

            viewModel.checkAnnouncement(newAnnouncement, action)
        }
    }

    private fun uiEventListener() {
        viewModel.events.observe(viewLifecycleOwner, {
            when (it) {
                is AnnouncementCreateViewModel.UIEvents.Idle ->
                    toggleProgressBar(false)
                is AnnouncementCreateViewModel.UIEvents.Loading ->
                    toggleProgressBar(true)
                is AnnouncementCreateViewModel.UIEvents.Success -> {
                    findNavController().navigateUp()
                    viewModel.setEventToIdle()
                }
                is AnnouncementCreateViewModel.UIEvents.Error -> {
                    showError(it.error)
                    viewModel.setEventToIdle()
                }
            }
        })
    }

    private fun showError(error: String) {
        val titleError = error.contains("judul")
        val descError = error.contains("deskripsi")
        val urlError = error.contains("url")

        if (titleError) {
            tilCreateAnnouncementTitle.isErrorEnabled = true
            tilCreateAnnouncementTitle.error = error
        } else if (descError) {
            tilCreateAnnouncementDesc.isErrorEnabled = true
            tilCreateAnnouncementDesc.error = error
        } else if (urlError) {
            tilCreateAnnouncementUrl.isErrorEnabled = true
            tilCreateAnnouncementUrl.error = error
        } else {
            snackBar.short(error)
        }
    }

    private fun hideError() {
        tilCreateAnnouncementTitle.isErrorEnabled = false
        tilCreateAnnouncementDesc.isErrorEnabled = false
        tilCreateAnnouncementUrl.isErrorEnabled = false
        tilCreateAnnouncementTitle.error = ""
        tilCreateAnnouncementDesc.error = ""
        tilCreateAnnouncementUrl.error = ""
    }

    private fun toggleProgressBar(show: Boolean) {
        if (show) {
            cvCreateAnnouncement.isEnabled = false
            llCreateAnnouncementProgress.visibility = View.VISIBLE
        } else {
            cvCreateAnnouncement.isEnabled = true
            llCreateAnnouncementProgress.visibility = View.GONE
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