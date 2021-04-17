package com.charuniverse.kelasku.ui.main.dev_tools.create_class

import android.webkit.URLUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.kelasku.data.firebase.ClassesRepository
import com.charuniverse.kelasku.data.models.Class
import kotlinx.coroutines.launch

class CreateClassViewModel : ViewModel() {

    sealed class UIEvents {
        object Idle : UIEvents()
        object Loading : UIEvents()
        object Complete : UIEvents()
        class Error(val error: String) : UIEvents()
    }

    private val _events = MutableLiveData<UIEvents>()
    val events: LiveData<UIEvents> = _events

    fun setEventToIdle() {
        _events.value = UIEvents.Idle
    }

    fun createClass(classObj: Class) {
        _events.value = UIEvents.Loading

        classObj.apply {
            if (name.isBlank()) {
                _events.value = UIEvents.Error("Anda belum memasukkan nama kelas")
                return@apply
            }

            if (code.isBlank()) {
                _events.value = UIEvents.Error("Anda belum memasukkan kode kelas")
                return@apply
            }

            if (endpoint.isBlank() || !URLUtil.isValidUrl(endpoint)) {
                _events.value =
                    UIEvents.Error("Format url yang anda masukkan salah (harus menggunakan http://)")
                return@apply
            }

            if (token.isBlank()) {
                _events.value = UIEvents.Error("Anda belum memasukkan token kelas")
                return@apply
            }

            addClass(this)
        }
    }

    private fun addClass(classObj: Class) = viewModelScope.launch {
        _events.value = try {
            ClassesRepository.addClass(classObj)
            UIEvents.Complete
        } catch (e: Exception) {
            UIEvents.Error(e.message.toString())
        }
    }

}