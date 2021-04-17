package com.charuniverse.kelasku.util.firebase.authentication

import com.charuniverse.kelasku.util.AppPreferences
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

object AuthenticationUtil {

    private val authRef: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    fun isUserSignedIn(): Boolean {
        return authRef.currentUser != null
    }

    suspend fun register(email: String, password: String) {
        authRef.createUserWithEmailAndPassword(email, password).await()
    }

    suspend fun login(email: String, password: String) {
        authRef.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun resetPassword(email: String) {
        authRef.sendPasswordResetEmail(email).await()
    }

    fun logout() {
        AppPreferences.deleteUserInfo()
        authRef.signOut()
    }

}