package com.charuniverse.kelasku.data.models

data class Assignment(
    val createDate: Long = System.currentTimeMillis(),
    val course: String? = null,
    val name: String? = null,
    val description: String? = null,
    val url: String? = null
)
