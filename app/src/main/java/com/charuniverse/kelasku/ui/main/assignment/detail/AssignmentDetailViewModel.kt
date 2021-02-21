package com.charuniverse.kelasku.ui.main.assignment.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.kelasku.data.firebase.AssignmentRepository
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

    fun deleteAssignment(id: String) = viewModelScope.launch {
        _events.value = UIEvents.Loading
        _events.value = try {
            AssignmentRepository.deleteAssignment(id)
            UIEvents.Success
        } catch (e: Exception) {
            UIEvents.Error(e.message.toString())
        }
    }

    fun addToIgnoreList(id: String) = viewModelScope.launch {
        _events.value = UIEvents.Loading
        _events.value = try {
            AssignmentRepository.addToIgnoreList(id)
            UIEvents.Success
        } catch (e: Exception) {
            UIEvents.Error(e.message.toString())
        }
    }
}