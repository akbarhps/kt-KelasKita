package com.charuniverse.kelasku.ui.main.announcement.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.kelasku.data.firebase.AnnouncementRepository
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

    fun deleteAnnouncement(id: String) = viewModelScope.launch {
        _events.value = UIEvents.Loading
        _events.value = try {
            AnnouncementRepository.deleteAnnouncement(id)
            UIEvents.Success
        } catch (e: Exception) {
            UIEvents.Error(e.message.toString())
        }
    }

}