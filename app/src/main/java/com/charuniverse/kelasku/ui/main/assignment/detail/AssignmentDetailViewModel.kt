package com.charuniverse.kelasku.ui.main.assignment.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.kelasku.data.firebase.AssignmentRepository
import com.charuniverse.kelasku.data.models.Assignment
import com.charuniverse.kelasku.util.AppPreferences
import com.charuniverse.kelasku.util.Constants
import com.charuniverse.kelasku.util.Globals
import com.charuniverse.kelasku.util.helper.ContentPermission
import com.charuniverse.kelasku.util.helper.ErrorStates
import kotlinx.coroutines.launch

class AssignmentDetailViewModel : ViewModel() {

    sealed class UIEvents {
        object Idle : UIEvents()
        object Loading : UIEvents()
        object Complete : UIEvents()
        class Error(
            val state: ErrorStates,
            val message: String? = null
        ) : UIEvents()
    }

    private val _events = MutableLiveData<UIEvents>(UIEvents.Idle)
    val events: LiveData<UIEvents> = _events

    private val _assignment = MutableLiveData<Assignment>()
    val assignment: LiveData<Assignment> = _assignment
    private lateinit var assignmentId: String

    var permission = ContentPermission()
    lateinit var shareUrl: String

    fun setEventToIdle() {
        _events.value = UIEvents.Idle
    }

    fun argsHandler(args: AssignmentDetailFragmentArgs) {
        val id = args.id
        val assignment = args.assignment

        assignment?.let {
            assignmentId = it.id
            getAccessPermission(assignment)
            _assignment.value = assignment
        }

        id?.let {
            assignmentId = it
            getAssignmentById()
        }

        shareUrl = Constants.ASSIGNMENT_URL + assignmentId
    }

    fun getAssignmentById() = viewModelScope.launch {
        _events.value = UIEvents.Loading
        _events.value = try {
            val assignment = AssignmentRepository.getAssignmentById(assignmentId)
            if (assignment == null) {
                _events.value = UIEvents.Error(ErrorStates.NOT_FOUND)
                return@launch
            }

            getAccessPermission(assignment)
            if (!permission.READ) {
                _events.value = UIEvents.Error(ErrorStates.NO_ACCESS)
                return@launch
            }

            _assignment.value = assignment
            UIEvents.Idle
        } catch (e: Exception) {
            UIEvents.Error(ErrorStates.NETWORK_ERROR, e.message.toString())
        }
    }

    fun addToIgnoreList() = viewModelScope.launch {
        _events.value = UIEvents.Loading
        _events.value = try {
            AssignmentRepository.addUserToHideList(assignmentId)
            Globals.refreshAssignment = true
            UIEvents.Complete
        } catch (e: Exception) {
            UIEvents.Error(ErrorStates.NETWORK_ERROR, e.message.toString())
        }
    }

    fun deleteAssignment() = viewModelScope.launch {
        _events.value = UIEvents.Loading
        _events.value = try {
            AssignmentRepository.deleteAssignment(assignmentId)
            Globals.refreshAssignment = true
            UIEvents.Complete
        } catch (e: Exception) {
            UIEvents.Error(ErrorStates.NETWORK_ERROR, e.message.toString())
        }
    }

    private fun getAccessPermission(obj: Assignment? = null) {
        val pref = AppPreferences
        val assignment = obj ?: _assignment.value ?: return

        val hasReadPermission = assignment.classCode == pref.userClassCode

        val isAdmin = pref.isUserAdmin

        // check if assignment from e-learning or from user
        // e-learning assignment has no creator
        val hasUpdatePermission = hasReadPermission && isAdmin
                && assignment.createdBy.isNotEmpty()

        permission = ContentPermission(
            hasReadPermission, hasUpdatePermission, hasUpdatePermission
        )
    }
}