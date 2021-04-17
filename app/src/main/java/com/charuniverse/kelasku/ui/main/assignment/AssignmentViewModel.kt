package com.charuniverse.kelasku.ui.main.assignment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.kelasku.data.firebase.AssignmentRepository
import com.charuniverse.kelasku.data.models.Assignment
import com.charuniverse.kelasku.util.AppPreferences
import com.charuniverse.kelasku.util.Globals
import com.charuniverse.kelasku.util.helper.ContentPermission
import kotlinx.coroutines.launch

class AssignmentViewModel : ViewModel() {

    sealed class UIEvents {
        object Idle : UIEvents()
        object Loading : UIEvents()
        object NoData : UIEvents()
        class Error(val message: String) : UIEvents()
    }

    private val _events = MutableLiveData<UIEvents>(UIEvents.Idle)
    val event: LiveData<UIEvents> = _events

    fun setEventToIdle() {
        _events.value = UIEvents.Idle
    }

    private val _assignments = MutableLiveData<List<Assignment>>(listOf())
    val assignments: LiveData<List<Assignment>> = _assignments

    fun getAssignment() = viewModelScope.launch {
        _events.value = UIEvents.Loading

        try {
            val documents = AssignmentRepository.getAssignment()
            Globals.refreshAssignment = false
            filterData(documents)
        } catch (e: Exception) {
            _events.value = UIEvents.Error(e.message.toString())
        }
    }

    private fun filterData(data: List<Assignment>) {
        val filteredData = data.filter {
            !it.hideList.contains(AppPreferences.userEmail)
        }

        _assignments.value = filteredData

        _events.value = if (filteredData.isEmpty()) {
            UIEvents.NoData
        } else {
            UIEvents.Idle
        }
    }
}