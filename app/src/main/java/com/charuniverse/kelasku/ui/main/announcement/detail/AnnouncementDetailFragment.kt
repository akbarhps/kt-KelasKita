package com.charuniverse.kelasku.ui.main.announcement.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.data.models.Announcement
import com.charuniverse.kelasku.ui.main.MainActivity
import com.charuniverse.kelasku.util.AppPreferences
import com.charuniverse.kelasku.util.Constants
import com.charuniverse.kelasku.util.Globals
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_announcement_detail.*
import java.text.SimpleDateFormat
import java.util.*

class AnnouncementDetailFragment : Fragment(R.layout.fragment_announcement_detail) {

    private var announcementId: String? = null
    private var announcementCode: String? = null
    private lateinit var viewModel: AnnouncementDetailViewModel
    private val args: AnnouncementDetailFragmentArgs by navArgs()

    enum class SnackBarType {
        BASIC,
        HIDE_ITEM,
        DELETE_ITEM,
        NETWORK_ERROR
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toggleNavigationBar(false)
        setHasOptionsMenu(true)

        viewModel = ViewModelProvider(requireActivity())
            .get(AnnouncementDetailViewModel::class.java)
            .also { it.argsHandler(args) }

        uiEventsListener()
        announcementListener()
    }

    private fun uiEventsListener() {
        viewModel.events.observe(viewLifecycleOwner, {
            when (it) {
                is AnnouncementDetailViewModel.UIEvents.Loading -> toggleProgressBar(true)
                is AnnouncementDetailViewModel.UIEvents.Success -> {
                    toggleProgressBar(false)
                    if (Globals.refreshAnnouncement) {
                        findNavController().navigateUp()
                    }
                }
                is AnnouncementDetailViewModel.UIEvents.Error -> {
                    buildSnackBar(it.message, Snackbar.LENGTH_INDEFINITE)
                    toggleProgressBar(false)
                }
                else -> toggleProgressBar(false)
            }
        })
    }

    private fun announcementListener() {
        viewModel.announcement.observe(viewLifecycleOwner, {
            announcementId = it.id
            announcementCode = it.classCode
            uiHandler(it)
        })
    }

    private fun uiHandler(announcement: Announcement) {
        val creator = "Oleh : ${announcement.creator}"

        tvAnnouncementDetailCreator.text = creator
        tvAnnouncementDetailDate.text = convertLongToDate(announcement.createTimestamp)
        tvAnnouncementDetailTitle.text = announcement.title
        tvAnnouncementDetailBody.text = announcement.body

        if (announcement.url.isEmpty()) return

        cvAnnouncementDetailUrl.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(announcement.url)))
            }
        }
    }

    private fun toggleProgressBar(isLoading: Boolean) {
        announcementDetailProgressBar.visibility = if (isLoading) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (AppPreferences.isDeveloper || (AppPreferences.isUserAdmin)) {
            inflater.inflate(R.menu.toolbar_admin_menu_announcement, menu)
        } else {
            inflater.inflate(R.menu.toolbar_menu_announcement, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_announcement_hide -> buildSnackBar(type = SnackBarType.HIDE_ITEM)
            R.id.menu_announcement_delete -> buildSnackBar(type = SnackBarType.DELETE_ITEM)
            R.id.menu_announcement_share -> {
                val intent = Intent().apply {
                    type = "text/plain"
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, Constants.ANNOUNCEMENT_URL + announcementId)
                }
                startActivity(Intent.createChooser(intent, "Bagikan ke:"))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun buildSnackBar(
        message: String = "Apakah anda yakin?",
        length: Int = Snackbar.LENGTH_SHORT,
        type: SnackBarType = SnackBarType.BASIC,
    ) {
        val snackBarType = if (message.contains(".")) {
            SnackBarType.NETWORK_ERROR
        } else {
            type
        }

        val snackBar = Snackbar.make(requireView(), message, length)
        when (snackBarType) {
            SnackBarType.HIDE_ITEM -> snackBar.setAction("Sembunyikan") {
                viewModel.hideAnnouncement()
            }
            SnackBarType.DELETE_ITEM -> snackBar.setAction("Hapus") {
                viewModel.deleteAnnouncement()
            }
            SnackBarType.NETWORK_ERROR -> snackBar.setAction("Try Again") {
                viewModel.getAnnouncementById()
            }
            else -> Unit
        }
        snackBar.show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertLongToDate(time: Long): String {
        val date = Date(time * 1000)
        return "Dikirim pada: " + SimpleDateFormat("dd/MM/yyy HH:mm").format(date)
    }

    private fun toggleNavigationBar(show: Boolean) {
        (activity as MainActivity).toggleNavigationBar(show)
    }

    override fun onDetach() {
        toggleNavigationBar(true)
        super.onDetach()
    }
}