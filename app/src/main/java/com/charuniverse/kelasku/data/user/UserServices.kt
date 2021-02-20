package com.charuniverse.kelasku.data.user

import com.charuniverse.kelasku.data.models.User

interface UserServices {
    suspend fun getUsers(): List<User>
    suspend fun getCurrentUser(nim: String): User?
}