package com.charuniverse.kelasku.util.firebase.messaging

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

object MessagingUtil {

    private val messagingRef: FirebaseMessaging by lazy {
        FirebaseMessaging.getInstance()
    }

    suspend fun subscribeToTopic(topic: String) {
        messagingRef.subscribeToTopic(topic).await()
    }

    suspend fun unsubscribeToTopic(topic: String) {
        messagingRef.unsubscribeFromTopic(topic).await()
    }
}