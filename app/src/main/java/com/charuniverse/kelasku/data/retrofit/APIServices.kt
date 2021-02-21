package com.charuniverse.kelasku.data.retrofit

import com.charuniverse.kelasku.data.models.Announcement
import com.charuniverse.kelasku.data.models.Assignment
import retrofit2.http.Body
import retrofit2.http.POST

interface APIServices {
    @POST("/assignment")
    suspend fun createAssignment(@Body assignment: Assignment)

    @POST("/announcement")
    suspend fun createAnnouncement(@Body announcement: Announcement)
}