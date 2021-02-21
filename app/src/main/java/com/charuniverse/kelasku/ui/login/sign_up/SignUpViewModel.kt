package com.charuniverse.kelasku.ui.login.sign_up

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.kelasku.data.firebase.ClassesRepository
import com.charuniverse.kelasku.data.firebase.UserRepository
import com.charuniverse.kelasku.data.models.Class
import com.charuniverse.kelasku.data.models.User
import com.charuniverse.kelasku.util.AppPreferences
import com.charuniverse.kelasku.util.firebase.authentication.AuthenticationUtil
import com.charuniverse.kelasku.util.firebase.messaging.MessagingUtil
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {

    sealed class UIEvents {
        object Idle : UIEvents()
        object NoData : UIEvents()
        object Loading : UIEvents()
        object Success : UIEvents()
        class Error(val error: String) : UIEvents()
    }

    private val _events = MutableLiveData<UIEvents>(UIEvents.Idle)
    val events: LiveData<UIEvents> = _events

    private val _classes = MutableLiveData<List<Class>>(listOf())
    val classes: LiveData<List<Class>> = _classes

    init {
        getAvailableClass()
    }

    fun getAvailableClass() = viewModelScope.launch {
        if (_classes.value!!.isNotEmpty()) {
            return@launch
        }
        try {
            _classes.value = ClassesRepository.getClasses()
        } catch (e: Exception) {
            _events.value = UIEvents.NoData
        }
    }

    fun getClassName(): List<String> {
        val className = mutableListOf<String>()
        _classes.value!!.forEach {
            className.add(it.name)
        }
        return className
    }

    fun getClassInfoByPosition(position: Int): Class {
        return _classes.value?.get(position)!!
    }

    fun signUp(email: String, password: String, className: String, classCode: String) {
        _events.value = UIEvents.Loading
        registerUser(email, password, className, classCode)
    }

    private fun registerUser(
        email: String, password: String,
        className: String, classCode: String
    ) = viewModelScope.launch {
        val user = User(email, className, classCode)

        _events.value = try {
            AuthenticationUtil.register(email, password)
            UserRepository.addUser(user)
            MessagingUtil.subscribeToTopic(classCode)
            AppPreferences.saveUserInfo(user)
            UIEvents.Success
        } catch (e: Exception) {
            UIEvents.Error(e.message.toString())
        }
    }
}