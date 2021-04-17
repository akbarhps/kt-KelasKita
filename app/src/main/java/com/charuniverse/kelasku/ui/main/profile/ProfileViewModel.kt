package com.charuniverse.kelasku.ui.main.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.kelasku.data.firebase.UserRepository
import com.charuniverse.kelasku.util.AppPreferences
import com.charuniverse.kelasku.util.firebase.authentication.AuthenticationUtil
import com.charuniverse.kelasku.util.firebase.messaging.MessagingUtil
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    sealed class UIEvents {
        object Idle : UIEvents()
        object Loading : UIEvents()
        object Success : UIEvents()
        object Complete : UIEvents()
        class Error(val message: String) : UIEvents()
    }

    private val _events = MutableLiveData<UIEvents>()
    val events: LiveData<UIEvents> = _events

    fun setEventToIdle() {
        _events.value = UIEvents.Idle
    }

    fun refreshUserInfo() = viewModelScope.launch {
        _events.value = UIEvents.Loading
        _events.value = try {
            val userData = UserRepository.getCurrentUser()
            MessagingUtil.subscribeToTopic(userData.classCode)
            MessagingUtil.subscribeToTopic("All")
            AppPreferences.saveUserInfo(userData)
            UIEvents.Success
        } catch (e: Exception) {
            UIEvents.Error(e.message.toString())
        }
    }

    fun logUserOut() = viewModelScope.launch {
        _events.value = UIEvents.Loading
        _events.value = try {
            MessagingUtil.unsubscribeToTopic(AppPreferences.userClassCode)
            MessagingUtil.unsubscribeToTopic("All")
            AuthenticationUtil.logout()
            UIEvents.Complete
        } catch (e: Exception) {
            UIEvents.Error(e.message.toString())
        }
    }
}