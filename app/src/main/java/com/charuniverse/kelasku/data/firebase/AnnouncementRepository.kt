package com.charuniverse.kelasku.data.firebase

import com.charuniverse.kelasku.data.models.Announcement
import com.charuniverse.kelasku.util.AppPreferences
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
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
            .whereIn("classCode", listOf(userClassCode, "All"))
            .orderBy("createTimestamp", Query.Direction.DESCENDING).get().await()
        return documents.toObjects(Announcement::class.java)
    }

    suspend fun getAnnouncementById(id: String): Announcement? {
        val document = announcementRef.document(id).get().await()
        return document.toObject(Announcement::class.java)
    }

    suspend fun hideAnnouncement(id: String) {
        announcementRef.document(id)
            .update(
                "hideList", FieldValue.arrayUnion(AppPreferences.userEmail)
            ).await()
    }

    suspend fun deleteAnnouncement(id: String) {
        announcementRef.document(id).delete().await()
    }
}