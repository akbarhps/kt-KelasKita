package com.charuniverse.kelasku.ui.main.announcement.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.kelasku.data.firebase.AnnouncementRepository
import com.charuniverse.kelasku.data.models.Announcement
import com.charuniverse.kelasku.util.AppPreferences
import com.charuniverse.kelasku.util.Globals
import kotlinx.coroutines.launch

class AnnouncementDetailViewModel : ViewModel() {

    sealed class UIEvents {
        object Idle : UIEvents()
        object Loading : UIEvents()
        object Success : UIEvents()
        class Error(val message: String) : UIEvents()
    }

    private val _events = MutableLiveData<UIEvents>(UIEvents.Idle)
    val events: LiveData<UIEvents> = _events

    private val _announcement = MutableLiveData<Announcement>()
    val announcement: LiveData<Announcement> = _announcement

    private lateinit var announcementId: String
    private var hasReadAccess = true
    private var hasDeleteAccess = true

    fun argsHandler(args: AnnouncementDetailFragmentArgs) {
        val id = args.id
        val announcement = args.announcement

        if (announcement != null) {
            announcementId = announcement.id
            _announcement.value = announcement
        }

        if (id != null) {
            announcementId = id
            getAnnouncementById()
        }
    }

    fun getAnnouncementById() = viewModelScope.launch {
        _events.value = UIEvents.Loading

        _events.value = try {
            val announcement = AnnouncementRepository.getAnnouncementById(announcementId)
            if (announcement == null) {
                _events.value = UIEvents.Error("Tugas Tidak Ditemukan 😭")
                return@launch
            }

            hasReadAccess = AppPreferences.isDeveloper || announcement.classCode ==
                    AppPreferences.userClassCode || announcement.classCode == "All"

            hasDeleteAccess = AppPreferences.isDeveloper || (announcement.classCode ==
                    AppPreferences.userClassCode && announcement.classCode != "All")

            if (!hasReadAccess) {
                _events.value =
                    UIEvents.Error("❌ Anda Tidak Dapat Mengakses Pemberitahuan Tersebut ❌")
                return@launch
            }

            _announcement.value = announcement

            UIEvents.Idle
        } catch (e: Exception) {
            UIEvents.Error(e.message.toString())
        }
    }

    fun hideAnnouncement() = viewModelScope.launch {
        _events.value = UIEvents.Loading

        if (!hasReadAccess) {
            _events.value =
                UIEvents.Error("❌ Anda tidak memiliki akses untuk menyembunyikan pemberitahuan ❌")
            return@launch
        }

        _events.value = try {
            AnnouncementRepository.hideAnnouncement(announcementId)
            Globals.refreshAnnouncement = true
            UIEvents.Success
        } catch (e: Exception) {
            UIEvents.Error(e.message.toString())
        }
    }

    fun deleteAnnouncement() = viewModelScope.launch {
        _events.value = UIEvents.Loading

        if (!hasDeleteAccess) {
            _events.value = UIEvents.Error("❌ Anda tidak memiliki akses untuk menghapus ❌")
            return@launch
        }

        _events.value = try {
            AnnouncementRepository.deleteAnnouncement(announcementId)
            Globals.refreshAnnouncement = true
            UIEvents.Success
        } catch (e: Exception) {
            UIEvents.Error(e.message.toString())
        }
    }

}