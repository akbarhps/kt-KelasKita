package com.charuniverse.kelasku.ui.login.sign_in

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.kelasku.data.firebase.UserRepository
import com.charuniverse.kelasku.util.AppPreferences
import com.charuniverse.kelasku.util.firebase.authentication.AuthenticationUtil
import com.charuniverse.kelasku.util.firebase.messaging.MessagingUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {

    sealed class UIEvents {
        object Idle : UIEvents()
        object Loading : UIEvents()
        object Success : UIEvents()
        class Error(val error: String) : UIEvents()
    }

    private val _events = MutableLiveData<UIEvents>(UIEvents.Idle)
    val events: LiveData<UIEvents> = _events

    fun signIn(email: String, password: String) {
        _events.value = UIEvents.Loading
        authenticateUser(email, password)
    }

    private fun authenticateUser(email: String, password: String) = viewModelScope.launch {
        _events.value = try {
            AuthenticationUtil.login(email, password)
            val user = UserRepository.getCurrentUser(email)
            MessagingUtil.subscribeToTopic(user.classCode)
            MessagingUtil.subscribeToTopic("All")
            AppPreferences.saveUserInfo(user)
            UIEvents.Success
        } catch (e: Exception) {
            UIEvents.Error(e.message.toString())
        }
    }
}