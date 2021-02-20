package com.charuniverse.kelasku.data.assignment

import com.charuniverse.kelasku.data.models.Assignment
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object AssignmentRepository: AssignmentServices {

    private val firebaseRef: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val assignmentRef: CollectionReference by lazy {
        firebaseRef.collection("Assignment")
    }

    override suspend fun getAssignment(): List<Assignment> {
        val documents = assignmentRef.get().await()
        return documents.toObjects(Assignment::class.java)
    }

    override suspend fun createAssignment(assignment: Assignment) {
        assignmentRef.document(assignment.createDate.toString())
            .set(assignment).await()
    }

    override suspend fun editAssignment(assignment: Assignment) {
        assignmentRef.document(assignment.createDate.toString())
            .set(assignment).await()
    }

    override suspend fun deleteAssignment(assignment: Assignment) {
        assignmentRef.document(assignment.createDate.toString())
            .delete().await()
    }
}