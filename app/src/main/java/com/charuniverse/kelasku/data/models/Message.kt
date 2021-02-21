package com.charuniverse.kelasku.data.models

import com.charuniverse.kelasku.util.AppPreferences

data class Message(
    val title: String = "",
    val body: String = "",
    val topic: String = AppPreferences.userClassCode
)
