package com.charuniverse.kelasku.ui.main.announcement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.kelasku.data.firebase.AnnouncementRepository
import com.charuniverse.kelasku.data.models.Announcement
import com.charuniverse.kelasku.util.AppPreferences
import com.charuniverse.kelasku.util.Globals
import kotlinx.coroutines.launch

class AnnouncementViewModel : ViewModel() {

    sealed class UIEvents {
        object Idle : UIEvents()
        object Loading : UIEvents()
        object NoData : UIEvents()
        class Error(val error: String) : UIEvents()
    }

    private val _events = MutableLiveData<UIEvents>(UIEvents.Idle)
    val events: LiveData<UIEvents> = _events

    fun setEventToIdle() {
        _events.value = UIEvents.Idle
    }

    private val _announcements = MutableLiveData<List<Announcement>>(listOf())
    val announcements: LiveData<List<Announcement>> = _announcements

    fun getAnnouncement() = viewModelScope.launch {
        _events.value = UIEvents.Loading

        try {
            val documents = AnnouncementRepository.getAnnouncement()
            Globals.refreshAnnouncement = false
            filterData(documents)
        } catch (e: Exception) {
            _events.value = UIEvents.Error(e.message.toString())
        }
    }

    private fun filterData(data: List<Announcement>) {
        val userEmail = AppPreferences.userEmail

        val filteredData = data.filter {
            !it.hideList.contains(userEmail)
        }

        _announcements.value = filteredData

        _events.value = if (filteredData.isEmpty()) {
            UIEvents.NoData
        } else {
            UIEvents.Idle
        }
    }
}