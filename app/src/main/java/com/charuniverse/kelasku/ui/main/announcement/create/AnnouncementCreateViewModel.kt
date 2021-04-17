package com.charuniverse.kelasku.ui.main.announcement.create

import android.webkit.URLUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.kelasku.data.firebase.AnnouncementRepository
import com.charuniverse.kelasku.data.models.Announcement
import com.charuniverse.kelasku.data.retrofit.RetrofitBuilder
import com.charuniverse.kelasku.util.Globals
import kotlinx.coroutines.launch

class AnnouncementCreateViewModel : ViewModel() {

    sealed class UIEvents {
        object Idle : UIEvents()
        object Loading : UIEvents()
        object Success : UIEvents()
        class Error(val error: String) : UIEvents()
    }

    enum class Action {
        ADD, EDIT
    }

    private val _events = MutableLiveData<UIEvents>(UIEvents.Idle)
    val events: LiveData<UIEvents> = _events

    fun setEventToIdle() {
        _events.value = UIEvents.Idle
    }

    fun checkAnnouncement(announcement: Announcement, action: Action) {
        _events.value = UIEvents.Loading

        announcement.apply {
            if (title.isBlank()) {
                _events.value = UIEvents.Error("Anda belum memasukkan judul pemberitahuan")
                return@apply
            }

            if (body.isBlank()) {
                _events.value = UIEvents.Error("Anda belum memasukkan deskripsi pemberitahuan")
                return@apply
            }

            if (url.isNotEmpty() && !URLUtil.isValidUrl(url)) {
                _events.value =
                    UIEvents.Error("Format url yang anda masukkan salah (harus menggunakan http://)")
                return@apply
            }

            if (action == Action.ADD) {
                addAnnouncement(this)
            } else {
                updateAnnouncement(this)
            }
        }
    }

    private fun addAnnouncement(announcement: Announcement) = viewModelScope.launch {
        _events.value = try {
            RetrofitBuilder.get().createAnnouncement(announcement)
            Globals.refreshAnnouncement = true
            UIEvents.Success
        } catch (e: Exception) {
            UIEvents.Error(e.message.toString())
        }
    }

    private fun updateAnnouncement(announcement: Announcement) = viewModelScope.launch {
        _events.value = try {
            AnnouncementRepository.updateAnnouncement(announcement)
            Globals.announcementUpdated = true
            Globals.refreshAnnouncement = true
            UIEvents.Success
        } catch (e: Exception) {
            UIEvents.Error(e.message.toString())
        }
    }
}