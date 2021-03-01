package com.charuniverse.kelasku.data.firebase

import com.charuniverse.kelasku.data.models.Assignment
import com.charuniverse.kelasku.util.AppPreferences
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

object AssignmentRepository {

    private val firebaseRef: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val assignmentRef: CollectionReference by lazy {
        firebaseRef.collection("Assignments")
    }

    suspend fun getAssignment(): List<Assignment> {
        val lastWeekInSeconds = System.currentTimeMillis() / 1000 - 604800
        val userClassCode = AppPreferences.userClassCode
        val documents = assignmentRef
            .whereEqualTo("classCode", userClassCode)
            .whereGreaterThanOrEqualTo("createTimestamp", lastWeekInSeconds)
            .orderBy("createTimestamp", Query.Direction.ASCENDING)
            .get().await()
        return documents.toObjects(Assignment::class.java)
    }

    suspend fun getAssignmentById(id: String): Assignment? {
        val document = assignmentRef.document(id).get().await()
        return document.toObject(Assignment::class.java)
    }

    suspend fun addToIgnoreList(id: String) {
        assignmentRef.document(id).update(
            "ignoreList", FieldValue.arrayUnion(AppPreferences.userEmail)
        ).await()
    }

    suspend fun deleteAssignment(id: String) {
        assignmentRef.document(id).delete().await()
    }
}