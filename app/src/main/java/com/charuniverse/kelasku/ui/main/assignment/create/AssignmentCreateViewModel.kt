package com.charuniverse.kelasku.ui.main.assignment.create

import android.webkit.URLUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.kelasku.data.models.Assignment
import com.charuniverse.kelasku.data.retrofit.RetrofitBuilder
import com.charuniverse.kelasku.ui.main.announcement.create.AnnouncementCreateViewModel
import com.charuniverse.kelasku.util.AppPreferences
import com.charuniverse.kelasku.util.Globals
import kotlinx.coroutines.launch

class AssignmentCreateViewModel : ViewModel() {

    sealed class UIEvents {
        object Idle : UIEvents()
        object Loading : UIEvents()
        object Success : UIEvents()
        class Error(val message: String) : UIEvents()
    }

    private val _events = MutableLiveData<UIEvents>(UIEvents.Idle)
    val events: LiveData<UIEvents> = _events

    fun createAssignment(assignment: Assignment) {
        _events.value = UIEvents.Loading

        assignment.apply {
            if(course.isBlank()) {
                _events.value = UIEvents.Error("Anda belum memasukkan matakuliah")
                return
            }

            if (title.isBlank()) {
                _events.value = UIEvents.Error("Anda belum memasukkan matakuliah")
                return
            }

            if (description.isBlank()) {
                _events.value = UIEvents.Error("Anda belum memasukkan judul tugas")
                return
            }

            if (url.isNotEmpty() && !URLUtil.isValidUrl(url)) {
                _events.value = UIEvents.Error("Format url yang anda masukkan salah (harus menggunakan http://)")
                return
            }

            addAssignment(this)
        }
    }

    private fun addAssignment(assignment: Assignment) = viewModelScope.launch {
        _events.value = try {
            RetrofitBuilder.get().createAssignment(assignment)
            Globals.refreshAssignment = true
            UIEvents.Success
        } catch (e: Exception) {
            UIEvents.Error(e.message.toString())
        }
    }

}