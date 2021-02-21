package com.charuniverse.kelasku.ui.main.announcement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.kelasku.data.firebase.AnnouncementRepository
import com.charuniverse.kelasku.data.models.Announcement
import kotlinx.coroutines.launch

class AnnouncementViewModel : ViewModel() {

    sealed class UIEvents {
        object Idle : UIEvents()
        object Loading : UIEvents()
        class Error(val error: String) : UIEvents()
    }

    private val _events = MutableLiveData<UIEvents>(UIEvents.Idle)
    val events: LiveData<UIEvents> = _events

    private val _announcements = MutableLiveData<List<Announcement>>(listOf())
    val announcements: LiveData<List<Announcement>> = _announcements

    init {
        if (_announcements.value!!.isEmpty()) {
            _events.value = UIEvents.Loading
            getAnnouncement()
        }
    }

    fun getAnnouncement() = viewModelScope.launch {
        _events.value = try {
            _announcements.value = AnnouncementRepository.getAnnouncement()
            UIEvents.Idle
        } catch (e: Exception) {
            UIEvents.Error(e.message.toString())
        }
    }
}