package com.charuniverse.kelasku.ui.main.announcement.create

import android.webkit.URLUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.kelasku.data.models.Announcement
import com.charuniverse.kelasku.data.retrofit.RetrofitBuilder
import kotlinx.coroutines.launch

class CreateAnnouncementViewModel : ViewModel() {

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
        if (announcement.title.isEmpty()) {
            _events.value =
                UIEvents.Error("Anda belum memasukkan judul pemberitahuan")
        } else if (announcement.body.isEmpty()) {
            _events.value =
                UIEvents.Error("Anda belum memasukkan deskripsi pemberitahuan")
        } else if (announcement.url.isNotEmpty() && !URLUtil.isValidUrl(announcement.url)) {
            _events.value =
                UIEvents.Error("Format url yang anda masukkan salah (harus menggunakan http://)")
        } else {
            addAnnouncement(announcement)
        }
    }

    private fun addAnnouncement(announcement: Announcement) = viewModelScope.launch {
        _events.value = try {
            RetrofitBuilder.get().createAnnouncement(announcement)
            UIEvents.Success
        } catch (e: Exception) {
            UIEvents.Error(e.message.toString())
        }
    }

}