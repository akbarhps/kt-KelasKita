package com.charuniverse.kelasku.data.models

data class User(
    val email: String = "",
    val className: String = "",
    val classCode: String = "",
    val admin: Boolean = false,
    val developer: Boolean = false
)
