package com.charuniverse.kelasku.ui.main.assignment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.kelasku.data.firebase.AssignmentRepository
import com.charuniverse.kelasku.data.models.Assignment
import com.charuniverse.kelasku.util.AppPreferences
import kotlinx.coroutines.launch

class AssignmentViewModel : ViewModel() {

    sealed class UIEvents {
        object Idle : UIEvents()
        object Loading : UIEvents()
        object NoData : UIEvents()
        class Error(val error: String) : UIEvents()
    }

    private val _events = MutableLiveData<UIEvents>(UIEvents.Idle)
    val event: LiveData<UIEvents> = _events

    private val _assignments = MutableLiveData<List<Assignment>>(listOf())
    val assignments: LiveData<List<Assignment>> = _assignments

    init {
        if (_assignments.value.isNullOrEmpty()) {
            _events.value = UIEvents.Loading
            getAssignment()
        }
    }

    fun getAssignment() = viewModelScope.launch {
        try {
            val documents = AssignmentRepository.getAssignment()
            filterData(documents)
        } catch (e: Exception) {
            _events.value = UIEvents.Error(e.message.toString())
        }
    }

    private fun filterData(data: List<Assignment>) {
        val userEmail = AppPreferences.userEmail
        val filteredData = mutableListOf<Assignment>()
        data.forEach {
            if (!it.ignoreList.contains(userEmail)) {
                filteredData.add(it)
            }
        }
        _assignments.value = filteredData
        _events.value = if (filteredData.isEmpty()) {
            UIEvents.NoData
        } else {
            UIEvents.Idle
        }
    }
}