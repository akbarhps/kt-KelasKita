package com.charuniverse.kelasku.data.models

import android.os.Parcelable
import com.charuniverse.kelasku.util.AppPreferences
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Announcement(
    val title: String = "",
    val body: String = "",
    val url: String = "",
    val classCode: String = AppPreferences.userClassCode,
    val creator: String = AppPreferences.userEmail,
    val id: String = (System.currentTimeMillis() / 1000).toString(),
    val createTimestamp: Long = System.currentTimeMillis() / 1000,
    val hideList: List<String> = listOf(),
) : Parcelable