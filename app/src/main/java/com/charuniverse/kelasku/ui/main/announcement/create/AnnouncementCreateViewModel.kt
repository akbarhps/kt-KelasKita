package com.charuniverse.kelasku.ui.main.announcement.create

import android.webkit.URLUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.kelasku.data.models.Announcement
import com.charuniverse.kelasku.data.retrofit.RetrofitBuilder
import com.charuniverse.kelasku.util.AppPreferences
import com.charuniverse.kelasku.util.Globals
import kotlinx.coroutines.launch

class AnnouncementCreateViewModel : ViewModel() {

    sealed class UIEvents {
        object Idle : UIEvents()
        object Loading : UIEvents()
        object Success : UIEvents()
        class Error(val error: String) : UIEvents()
    }

    private val _events = MutableLiveData<UIEvents>(UIEvents.Idle)
    val events: LiveData<UIEvents> = _events

    fun createAnnouncement(announcement: Announcement) {
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
                _events.value = UIEvents.Error("Format url yang anda masukkan salah (harus menggunakan http://)")
                return@apply
            }

            addAnnouncement(this)
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

}