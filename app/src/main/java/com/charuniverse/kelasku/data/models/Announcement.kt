package com.charuniverse.kelasku.data.models

import android.os.Parcelable
import com.charuniverse.kelasku.util.AppPreferences
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Announcement(
    var title: String = "",
    var body: String = "",
    var url: String = "",
    var classCode: String = AppPreferences.userClassCode,
    var editedBy: String = "",
    val hideList: List<String> = listOf(),
    var createdBy: String = AppPreferences.userEmail,
    val createTimestamp: Long = System.currentTimeMillis() / 1000,
    val id: String = (createTimestamp + Math.random().toInt()).toString()
) : Parcelable