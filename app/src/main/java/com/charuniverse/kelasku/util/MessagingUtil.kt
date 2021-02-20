package com.charuniverse.kelasku.util

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

object MessagingUtil {

    private val messagingRef: FirebaseMessaging by lazy {
        FirebaseMessaging.getInstance()
    }

    suspend fun subscribe() {
        messagingRef.subscribeToTopic(Constants.MESSAGING_TOPIC).await()
    }

    suspend fun unsubscribe() {
        messagingRef.unsubscribeFromTopic(Constants.MESSAGING_TOPIC).await()
    }
}