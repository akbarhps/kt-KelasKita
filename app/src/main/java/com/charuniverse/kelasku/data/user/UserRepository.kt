package com.charuniverse.kelasku.data.user

import com.charuniverse.kelasku.data.models.User
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object UserRepository : UserServices{

    private val firebaseRef: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val userRef: CollectionReference by lazy {
        firebaseRef.collection("Users")
    }

    override suspend fun getUsers(): List<User> {
        val documents = userRef.get().await()
        return documents.toObjects(User::class.java)
    }

    override suspend fun getCurrentUser(nim: String): User? {
        val document = userRef.document(nim).get().await()
        return document.toObject(User::class.java)
    }
}