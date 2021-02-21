package com.charuniverse.kelasku.ui.main.assignment.create

import android.webkit.URLUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.kelasku.data.models.Assignment
import com.charuniverse.kelasku.data.retrofit.RetrofitBuilder
import kotlinx.coroutines.launch

class CreateAssignmentViewModel : ViewModel() {

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
        if (assignment.course.isEmpty()) {
            _events.value = UIEvents.Error("Anda belum memasukkan matakuliah")
        } else if (assignment.title.isEmpty()) {
            _events.value = UIEvents.Error("Anda belum memasukkan judul tugas")
        } else if (assignment.description.isEmpty()) {
            _events.value = UIEvents.Error("Anda belum memasukkan deskripsi tugas")
        } else if (assignment.url.isNotEmpty() && !URLUtil.isValidUrl(assignment.url)) {
            _events.value =
                UIEvents.Error("Format url yang anda masukkan salah (harus menggunakan http://)")
        } else {
            addAssignment(assignment)
        }
    }

    private fun addAssignment(assignment: Assignment) = viewModelScope.launch {
        _events.value = try {
            RetrofitBuilder.get().createAssignment(assignment)
            UIEvents.Success
        } catch (e: Exception) {
            UIEvents.Error(e.message.toString())
        }
    }

}