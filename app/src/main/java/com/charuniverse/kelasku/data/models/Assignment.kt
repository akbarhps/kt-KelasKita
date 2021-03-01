package com.charuniverse.kelasku.data.models

import android.os.Parcelable
import com.charuniverse.kelasku.util.AppPreferences
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Assignment(
    val course: String = "",
    val title: String = "",
    val description: String = "",
    val url: String = "",
    val creator: String = "",
    val endTimestamp: Long = 0L,
    val ignoreList: List<String> = listOf(),
    val classCode: String = AppPreferences.userClassCode,
    val createTimestamp: Long = System.currentTimeMillis() / 1000,
    val id: String = (createTimestamp * endTimestamp + createTimestamp).toString(),
) : Parcelable
