package com.charuniverse.kelasku.data.assignment

import com.charuniverse.kelasku.data.models.Assignment

interface AssignmentServices {
    suspend fun getAssignment(): List<Assignment>
    suspend fun createAssignment(assignment: Assignment)
    suspend fun editAssignment(assignment: Assignment)
    suspend fun deleteAssignment(assignment: Assignment)
}