package com.charuniverse.kelasku.ui.main.announcement

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.data.models.Announcement
import com.charuniverse.kelasku.ui.main.MainActivity
import com.charuniverse.kelasku.util.AppPreferences
import com.charuniverse.kelasku.util.Globals
import com.charuniverse.kelasku.util.helper.SnackBarBuilder
import kotlinx.android.synthetic.main.fragment_announcement.*

class AnnouncementFragment : Fragment(R.layout.fragment_announcement),
    AnnouncementAdapter.AnnouncementEvents {

    override fun onItemClick(announcement: Announcement) {
        val direction = AnnouncementFragmentDirections
            .actionAnnouncementFragmentToAnnouncementDetailFragment(announcement)
        findNavController().navigate(direction)
    }

    private lateinit var viewModel: AnnouncementViewModel

    private lateinit var snackbar: SnackBarBuilder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        snackbar = SnackBarBuilder(view)
        viewModel = ViewModelProvider(requireActivity())
            .get(AnnouncementViewModel::class.java)

        uiEventListener()
        announcementListener()
        uiHandler()
    }

    private fun uiEventListener() {
        viewModel.events.observe(viewLifecycleOwner, {
            when (it) {
                is AnnouncementViewModel.UIEvents.Idle -> {
                    toggleProgressBar(false)
                    toggleNoDataState(false)
                }
                is AnnouncementViewModel.UIEvents.Loading -> {
                    toggleProgressBar(true)
                }
                is AnnouncementViewModel.UIEvents.NoData -> {
                    toggleProgressBar(false)
                    toggleNoDataState(true)
                }
                is AnnouncementViewModel.UIEvents.Error -> {
                    snackbar.actionShort(it.error, "Try Again") {
                        viewModel.getAnnouncement()
                    }
                    viewModel.setEventToIdle()
                }
            }
        })
    }

    private fun announcementListener() {
        viewModel.announcements.observe(viewLifecycleOwner, {
            val announcementAdapter = AnnouncementAdapter(this, it)
            recyclerAnnouncement.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = announcementAdapter
            }
        })
    }

    private fun uiHandler() {
        if (AppPreferences.isUserAdmin) {
            cvAnnouncementCreate.visibility = View.VISIBLE
        }

        srAnnouncement.setOnRefreshListener {
            viewModel.getAnnouncement()
        }

        cvAnnouncementCreate.setOnClickListener {
            val direction = AnnouncementFragmentDirections
                .actionAnnouncementFragmentToAnnouncementCreateFragment()
            findNavController().navigate(direction)
        }
    }

    private fun toggleNoDataState(show: Boolean) {
        viewNoAnnouncement.visibility = if (show) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun toggleProgressBar(show: Boolean) {
        srAnnouncement.isRefreshing = show
    }

    override fun onResume() {
        (activity as MainActivity).toggleNavigationBar(true)
        if (Globals.refreshAnnouncement) {
            viewModel.getAnnouncement()
        }
        super.onResume()
    }
}