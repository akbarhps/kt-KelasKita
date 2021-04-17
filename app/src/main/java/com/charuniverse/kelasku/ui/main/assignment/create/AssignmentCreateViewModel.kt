package com.charuniverse.kelasku.ui.main.assignment.create

import android.webkit.URLUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.kelasku.data.firebase.AssignmentRepository
import com.charuniverse.kelasku.data.models.Assignment
import com.charuniverse.kelasku.data.retrofit.RetrofitBuilder
import com.charuniverse.kelasku.util.Globals
import kotlinx.coroutines.launch

class AssignmentCreateViewModel : ViewModel() {

    sealed class UIEvents {
        object Idle : UIEvents()
        object Loading : UIEvents()
        object Complete : UIEvents()
        class Error(val message: String) : UIEvents()
    }

    enum class Action {
        ADD, EDIT
    }

    private val _events = MutableLiveData<UIEvents>(UIEvents.Idle)
    val events: LiveData<UIEvents> = _events

    fun setEventToIdle() {
        _events.value = UIEvents.Idle
    }

    fun checkAssignment(assignment: Assignment, action: Action) {
        _events.value = UIEvents.Loading

        assignment.apply {
            if (endTimestamp == 0L) {
                _events.value = UIEvents.Error("Anda belum memasukkan tanggal pengumpulan")
                return
            }

            if (course.isBlank()) {
                _events.value = UIEvents.Error("Anda belum memasukkan matakuliah")
                return
            }

            if (title.isBlank()) {
                _events.value = UIEvents.Error("Anda belum memasukkan judul tugas")
                return
            }

            if (description.isBlank()) {
                _events.value = UIEvents.Error("Anda belum memasukkan deskripsi tugas")
                return
            }

            if (url.isNotEmpty() && !URLUtil.isValidUrl(url)) {
                _events.value =
                    UIEvents.Error("Format url yang anda masukkan salah (harus menggunakan http://)")
                return
            }

            if (action == Action.ADD) {
                addAssignment(this)
            } else {
                updateAssignment(this)
            }
        }
    }

    private fun addAssignment(assignment: Assignment) = viewModelScope.launch {
        _events.value = try {
            RetrofitBuilder.get().createAssignment(assignment)
            Globals.refreshAssignment = true
            UIEvents.Complete
        } catch (e: Exception) {
            UIEvents.Error(e.message.toString())
        }
    }

    private fun updateAssignment(assignment: Assignment) = viewModelScope.launch {
        _events.value = try {
            AssignmentRepository.updateAssignment(assignment)
            Globals.refreshAssignment = true
            Globals.assignmentUpdated = true
            UIEvents.Complete
        } catch (e: Exception) {
            UIEvents.Error(e.message.toString())
        }
    }

}