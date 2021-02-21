package com.charuniverse.kelasku.data.firebase

import com.charuniverse.kelasku.data.models.Class
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object ClassesRepository {

    private val firebaseRef: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val classRef: CollectionReference by lazy {
        firebaseRef.collection("Classes")
    }

    suspend fun getClasses(): List<Class> {
        val documents = classRef.orderBy("name").get().await()
        return documents.toObjects(Class::class.java)
    }

}