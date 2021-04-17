package com.charuniverse.kelasku.ui.main.dev_tools.edit_user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.kelasku.data.firebase.UserRepository
import com.charuniverse.kelasku.data.models.User
import kotlinx.coroutines.launch

class EditUserViewModel : ViewModel() {

    sealed class UIEvents {
        object Idle : UIEvents()
        object Loading : UIEvents()
        object Success : UIEvents()
        class Error(val message: String) : UIEvents()
    }

    private val _events = MutableLiveData<UIEvents>()
    val events: LiveData<UIEvents> = _events

    fun setEventToIdle() {
        _events.value = UIEvents.Idle
    }

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    init {
        getAllUser()
    }

    private fun getAllUser() = viewModelScope.launch {
        _events.value = UIEvents.Loading
        _events.value = try {
            _users.value = UserRepository.getUsers()
            UIEvents.Idle
        } catch (e: Exception) {
            UIEvents.Error(e.message.toString())
        }
    }

    fun updateUserPermission(id: String, isAdmin: Boolean) = viewModelScope.launch {
        _events.value = UIEvents.Loading
        _events.value = try {
            UserRepository.updateUserPermission(id, isAdmin)
            getAllUser()
            UIEvents.Success
        } catch (e: Exception) {
            UIEvents.Error(e.message.toString())
        }
    }

}