package com.charuniverse.kelasku.ui.main.announcement.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.data.models.Announcement
import com.charuniverse.kelasku.ui.main.MainActivity
import com.charuniverse.kelasku.ui.main.dialog.ContentDetailMenuDialog
import com.charuniverse.kelasku.util.Globals
import com.charuniverse.kelasku.util.helper.Converter.convertLongToDate
import com.charuniverse.kelasku.util.helper.ErrorStates
import kotlinx.android.synthetic.main.fragment_announcement_detail.*
import kotlinx.android.synthetic.main.view_network_error.*
import kotlinx.android.synthetic.main.view_not_found.*

class AnnouncementDetailFragment : Fragment(R.layout.fragment_announcement_detail),
    ContentDetailMenuDialog.DetailMenuDialogEvents {

    private lateinit var viewModel: AnnouncementDetailViewModel
    private val args: AnnouncementDetailFragmentArgs by navArgs()

    private lateinit var menuDialogContent: ContentDetailMenuDialog
    private lateinit var editDestination: NavDirections

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideNavigationBar()

        viewModel = ViewModelProvider(requireActivity())
            .get(AnnouncementDetailViewModel::class.java)
            .also { it.argsHandler(args) }

        uiEventsListener()
        announcementListener()
    }

    private fun uiEventsListener() {
        viewModel.events.observe(viewLifecycleOwner, {
            when (it) {
                is AnnouncementDetailViewModel.UIEvents.Idle -> {
                    hideAllStates()
                    toggleProgressBar(false)
                }
                is AnnouncementDetailViewModel.UIEvents.Loading ->
                    toggleProgressBar(true)
                is AnnouncementDetailViewModel.UIEvents.Complete ->
                    findNavController().navigateUp()
                is AnnouncementDetailViewModel.UIEvents.Error -> {
                    hideAllStates()
                    toggleProgressBar(false)
                    when (it.states) {
                        ErrorStates.NOT_FOUND -> toggleNotFoundState(true)
                        ErrorStates.NO_ACCESS -> toggleNoAccessState(true)
                        ErrorStates.NETWORK_ERROR -> toggleNetworkErrorState(true)
                    }
                }
            }
        })
    }

    private fun announcementListener() {
        viewModel.announcement.observe(viewLifecycleOwner, {
            uiHandler(it)

            menuDialogContent = ContentDetailMenuDialog(viewModel.permission, this)

            editDestination = AnnouncementDetailFragmentDirections
                .actionAnnouncementDetailFragmentToAnnouncementCreateFragment(it)
        })

        cvNetworkErrorRetry.setOnClickListener {
            viewModel.getAnnouncementById()
        }
    }

    private fun uiHandler(announcement: Announcement) {
        tvAnnouncementDetailCreator.text = "📩\t: ${announcement.createdBy}"
        tvAnnouncementDetailDate.text = "⌚ \t" + convertLongToDate(announcement.createTimestamp)
        tvAnnouncementDetailTitle.text = announcement.title
        tvAnnouncementDetailBody.text = announcement.body

        if (announcement.url.isNotEmpty()) {
            cvAnnouncementDetailUrl.visibility = View.VISIBLE
        }

        cvAnnouncementDetailUrl.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(announcement.url)))
        }

        cvAnnouncementDetailMenu.visibility = View.VISIBLE
        cvAnnouncementDetailMenu.setOnClickListener {
            menuDialogContent.show(parentFragmentManager, null)
        }
    }

    private fun toggleProgressBar(isLoading: Boolean) {
        if (isLoading) {
            cvNetworkErrorRetry.isEnabled = false
            viewProgressBar.visibility = View.VISIBLE
            llNetworkErrorRetryProgress.visibility = View.VISIBLE
        } else {
            cvNetworkErrorRetry.isEnabled = true
            viewProgressBar.visibility = View.GONE
            llNetworkErrorRetryProgress.visibility = View.GONE
        }
    }

    private fun hideAllStates() {
        toggleNotFoundState(false)
        toggleNoAccessState(false)
        toggleNetworkErrorState(false)
    }

    private fun toggleNotFoundState(show: Boolean) {
        viewNotFound.visibility = if (show) {
            tvNotFoundTitle.text = "Pemberitahuan yang kamu cari tidak ditemukan"
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun toggleNoAccessState(show: Boolean) {
        viewNoAccess.visibility = if (show) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun toggleNetworkErrorState(show: Boolean) {
        viewNetworkError.visibility = if (show) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun onMenuShareClick() {
        startActivity(Intent.createChooser(Intent().apply {
            type = "text/plain"
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, viewModel.shareUrl)
        }, "Bagikan ke:"))
    }

    override fun onMenuHideClick() {
        viewModel.hideAnnouncement()
    }

    override fun onMenuEditClick() {
        findNavController().navigate(editDestination)
    }

    override fun onMenuDeleteClick() {
        viewModel.deleteAnnouncement()
    }

    private fun hideNavigationBar() {
        (activity as MainActivity).toggleNavigationBar(false)
    }

    override fun onResume() {
        super.onResume()
        if (Globals.announcementUpdated) {
            viewModel.getAnnouncementById()
            Globals.announcementUpdated = false
        }
    }

    override fun onDetach() {
        viewModel.setEventToIdle()
        super.onDetach()
    }
}