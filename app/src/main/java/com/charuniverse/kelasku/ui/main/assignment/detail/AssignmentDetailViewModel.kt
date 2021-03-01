package com.charuniverse.kelasku.ui.main.assignment.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.kelasku.data.firebase.AssignmentRepository
import com.charuniverse.kelasku.data.models.Assignment
import com.charuniverse.kelasku.util.AppPreferences
import com.charuniverse.kelasku.util.Globals
import kotlinx.coroutines.launch

class AssignmentDetailViewModel : ViewModel() {

    sealed class UIEvents {
        object Idle : UIEvents()
        object Loading : UIEvents()
        object Success : UIEvents()
        class Error(val message: String) : UIEvents()
    }

    private val _events = MutableLiveData<UIEvents>(UIEvents.Idle)
    val events: LiveData<UIEvents> = _events

    private val _assignment = MutableLiveData<Assignment>()
    val assignment: LiveData<Assignment> = _assignment

    private lateinit var assignmentId: String
    private var hasAccess = true

    fun argsHandler(args: AssignmentDetailFragmentArgs) {
        val id = args.id
        val assignment = args.assignment

        if (assignment != null) {
            assignmentId = assignment.id
            _assignment.value = assignment
        }

        if (id != null) {
            assignmentId = id
            getAssignmentById()
        }
    }

    fun getAssignmentById() = viewModelScope.launch {
        _events.value = UIEvents.Loading

        _events.value = try {
            val assignment = AssignmentRepository.getAssignmentById(assignmentId)
            if (assignment == null) {
                _events.value = UIEvents.Error("Tugas Tidak Ditemukan 😭")
                return@launch
            }

            hasAccess =
                AppPreferences.isDeveloper || assignment.classCode == AppPreferences.userClassCode

            if (!hasAccess) {
                _events.value = UIEvents.Error("❌ Anda Tidak Dapat Mengakses Tugas Tersebut ❌")
                return@launch
            }

            _assignment.value = assignment

            UIEvents.Idle
        } catch (e: Exception) {
            UIEvents.Error(e.message.toString())
        }
    }

    fun deleteAssignment() = viewModelScope.launch {
        _events.value = UIEvents.Loading

        if (!hasAccess) {
            _events.value = UIEvents.Error("❌ Anda tidak memiliki akses untuk menghapus ❌")
            return@launch
        }

        _events.value = try {
            AssignmentRepository.deleteAssignment(assignmentId)
            Globals.refreshAssignment = true
            UIEvents.Success
        } catch (e: Exception) {
            UIEvents.Error(e.message.toString())
        }
    }

    fun addToIgnoreList() = viewModelScope.launch {
        _events.value = UIEvents.Loading

        if (!hasAccess) {
            _events.value = UIEvents.Error("❌ Anda tidak memiliki akses untuk itu ❌")
            return@launch
        }

        _events.value = try {
            AssignmentRepository.addToIgnoreList(assignmentId)
            Globals.refreshAssignment = true
            UIEvents.Success
        } catch (e: Exception) {
            UIEvents.Error(e.message.toString())
        }
    }
}