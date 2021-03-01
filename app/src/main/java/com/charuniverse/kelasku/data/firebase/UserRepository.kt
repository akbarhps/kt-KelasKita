package com.charuniverse.kelasku.data.firebase

import com.charuniverse.kelasku.data.models.User
import com.charuniverse.kelasku.util.AppPreferences
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object UserRepository {

    private val firebaseRef: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val userRef: CollectionReference by lazy {
        firebaseRef.collection("Users")
    }

    suspend fun getUsers(): List<User> {
        val documents = userRef.get().await()
        return documents.toObjects(User::class.java)
    }

    suspend fun addUser(user: User) {
        userRef.document(user.email).set(user).await()
    }

    suspend fun getCurrentUser(email: String? = null): User {
        val userEmail = email ?: AppPreferences.userEmail
        val document = userRef.document(userEmail).get().await()
        return document.toObject(User::class.java)!!
    }
}