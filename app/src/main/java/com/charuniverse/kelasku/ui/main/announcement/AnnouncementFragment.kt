package com.charuniverse.kelasku.ui.main.announcement

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.data.models.Announcement
import com.charuniverse.kelasku.util.AppPreferences
import com.charuniverse.kelasku.util.Globals
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_announcement.*

class AnnouncementFragment : Fragment(R.layout.fragment_announcement),
    AnnouncementAdapter.AnnouncementEvents {

    private lateinit var viewModel: AnnouncementViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())
            .get(AnnouncementViewModel::class.java)

        if (AppPreferences.isUserAdmin) {
            cvAnnouncementCreate.visibility = View.VISIBLE
        }

        uiEventListener()
        uiManager()
    }

    private fun uiEventListener() {
        viewModel.events.observe(viewLifecycleOwner, {
            val isLoading = when (it) {
                is AnnouncementViewModel.UIEvents.Loading -> true
                is AnnouncementViewModel.UIEvents.NoData -> {
                    toggleNoDataState(true)
                    false
                }
                is AnnouncementViewModel.UIEvents.Error -> {
                    buildSnackBar(it.error)
                    false
                }
                else -> {
                    toggleNoDataState(false)
                    false
                }
            }
            toggleProgressBar(isLoading)
        })
    }

    private fun uiManager() {
        viewModel.announcements.observe(viewLifecycleOwner, {
            val announcementAdapter = AnnouncementAdapter(this, it)
            recyclerAnnouncement.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = announcementAdapter
            }
        })
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

    private fun buildSnackBar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
            .setAction("Refresh") { viewModel.getAnnouncement() }
            .show()
    }

    override fun onItemClick(announcement: Announcement) {
        val direction = AnnouncementFragmentDirections
            .actionAnnouncementFragmentToAnnouncementDetailFragment(announcement)
        findNavController().navigate(direction)
    }

    override fun onResume() {
        super.onResume()
        if (Globals.refreshAnnouncement) {
            viewModel.getAnnouncement()
        }
    }
}