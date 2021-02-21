package com.charuniverse.kelasku.ui.main.announcement

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.ui.main.announcement.create.CreateAnnouncementActivity
import com.charuniverse.kelasku.util.AppPreferences
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_announcement.*

class AnnouncementFragment : Fragment(R.layout.fragment_announcement) {

    private lateinit var baseView: View
    private lateinit var viewModel: AnnouncementViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        baseView = view
        viewModel = ViewModelProvider(requireActivity())
            .get(AnnouncementViewModel::class.java)

        val isAdmin = AppPreferences.isUserAdmin
        if (isAdmin) {
            fabCreateAnnouncement.visibility = View.VISIBLE
        }

        uiListener()
        eventsListener()
        announcementListener()
    }

    private fun eventsListener() {
        viewModel.events.observe(viewLifecycleOwner, {
            when (it) {
                is AnnouncementViewModel.UIEvents.Loading ->
                    toggleRefreshAnimation(true)
                is AnnouncementViewModel.UIEvents.Error -> {
                    buildSnackBar(it.error)
                    toggleRefreshAnimation(false)
                }
                else -> toggleRefreshAnimation(false)
            }
        })
    }

    private fun announcementListener() {
        viewModel.announcements.observe(viewLifecycleOwner, { announcements ->
            recyclerAnnouncement.let {
                it.layoutManager = LinearLayoutManager(requireContext())
                it.adapter = AnnouncementAdapter(requireActivity(), announcements)
            }
        })
    }

    private fun uiListener() {
        srAnnouncement.setOnRefreshListener {
            viewModel.getAnnouncement()
        }
        fabCreateAnnouncement.setOnClickListener {
            requireActivity()
                .startActivity(Intent(requireContext(), CreateAnnouncementActivity::class.java))
        }
    }

    private fun toggleRefreshAnimation(show: Boolean) {
        srAnnouncement.isRefreshing = show
    }

    private fun buildSnackBar(message: String) {
        Snackbar.make(baseView, message, Snackbar.LENGTH_LONG)
            .setAction("Refresh") {
                viewModel.getAnnouncement()
            }.show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAnnouncement()
    }
}