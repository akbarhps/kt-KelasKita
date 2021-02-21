package com.charuniverse.kelasku.data.firebase

import com.charuniverse.kelasku.data.models.Announcement
import com.charuniverse.kelasku.util.AppPreferences
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

object AnnouncementRepository {

    private val firebaseRef: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val announcementRef: CollectionReference by lazy {
        firebaseRef.collection("Announcements")
    }

    suspend fun getAnnouncement(): List<Announcement> {
        val userClassCode = AppPreferences.userClassCode
        val documents = announcementRef
            .whereEqualTo("classCode", userClassCode)
            .orderBy("createTimestamp", Query.Direction.DESCENDING).get().await()
        return documents.toObjects(Announcement::class.java)
    }

    suspend fun deleteAnnouncement(id: String) {
        announcementRef.document(id).delete().await()
    }
}