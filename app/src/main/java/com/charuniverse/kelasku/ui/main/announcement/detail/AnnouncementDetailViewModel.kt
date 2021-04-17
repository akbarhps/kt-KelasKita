package com.charuniverse.kelasku.ui.main.announcement.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.kelasku.data.firebase.AnnouncementRepository
import com.charuniverse.kelasku.data.models.Announcement
import com.charuniverse.kelasku.util.AppPreferences
import com.charuniverse.kelasku.util.Constants
import com.charuniverse.kelasku.util.Globals
import com.charuniverse.kelasku.util.helper.ContentPermission
import com.charuniverse.kelasku.util.helper.ErrorStates
import kotlinx.coroutines.launch

class AnnouncementDetailViewModel : ViewModel() {

    sealed class UIEvents {
        object Idle : UIEvents()
        object Loading : UIEvents()
        object Complete : UIEvents()
        class Error(
            val states: ErrorStates,
            val message: String? = null
        ) : UIEvents()
    }

    private val _events = MutableLiveData<UIEvents>(UIEvents.Idle)
    val events: LiveData<UIEvents> = _events

    private val _announcement = MutableLiveData<Announcement>()
    val announcement: LiveData<Announcement> = _announcement
    private lateinit var announcementId: String

    var permission = ContentPermission()
    lateinit var shareUrl: String

    fun setEventToIdle() {
        _events.value = UIEvents.Idle
    }

    fun argsHandler(args: AnnouncementDetailFragmentArgs) {
        val id = args.id
        val announcement = args.announcement

        if (announcement != null) {
            announcementId = announcement.id
            getAccessPermission(announcement)
            _announcement.value = announcement
        }

        if (id != null) {
            announcementId = id
            getAnnouncementById()
        }

        shareUrl = Constants.ANNOUNCEMENT_URL + announcementId
    }

    fun getAnnouncementById() = viewModelScope.launch {
        _events.value = UIEvents.Loading
        _events.value = try {
            val announcement = AnnouncementRepository.getAnnouncementById(announcementId)
            if (announcement == null) {
                _events.value = UIEvents.Error(ErrorStates.NOT_FOUND)
                return@launch
            }

            getAccessPermission(announcement)
            if (!permission.READ) {
                _events.value = UIEvents.Error(ErrorStates.NO_ACCESS)
                return@launch
            }

            _announcement.value = announcement
            UIEvents.Idle
        } catch (e: Exception) {
            UIEvents.Error(ErrorStates.NETWORK_ERROR, e.message.toString())
        }
    }

    fun hideAnnouncement() = viewModelScope.launch {
        _events.value = UIEvents.Loading
        _events.value = try {
            AnnouncementRepository.addUserToHideList(announcementId)
            Globals.refreshAnnouncement = true
            UIEvents.Complete
        } catch (e: Exception) {
            UIEvents.Error(ErrorStates.NETWORK_ERROR, e.message.toString())
        }
    }

    fun deleteAnnouncement() = viewModelScope.launch {
        _events.value = UIEvents.Loading
        _events.value = try {
            AnnouncementRepository.deleteAnnouncement(announcementId)
            Globals.refreshAnnouncement = true
            UIEvents.Complete
        } catch (e: Exception) {
            UIEvents.Error(ErrorStates.NETWORK_ERROR, e.message.toString())
        }
    }

    private fun getAccessPermission(obj: Announcement? = null) {
        val pref = AppPreferences
        val announcement = obj ?: _announcement.value ?: return

        val isDeveloper = pref.isDeveloper
        val isAdmin = pref.isUserAdmin

        val hasReadPermission =
            announcement.classCode == pref.userClassCode || announcement.classCode == "All"

        val hasUpdatePermission = isDeveloper ||
                (hasReadPermission && isAdmin && announcement.classCode != "All")

        permission = ContentPermission(
            hasReadPermission, hasUpdatePermission, hasUpdatePermission
        )
    }
}