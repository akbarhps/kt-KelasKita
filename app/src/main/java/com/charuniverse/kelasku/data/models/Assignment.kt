package com.charuniverse.kelasku.data.models

import android.os.Parcelable
import com.charuniverse.kelasku.util.AppPreferences
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Assignment(
    var course: String = "",
    var title: String = "",
    var description: String = "",
    var url: String = "",
    var endTimestamp: Long = 0L,
    var createdBy: String = "",
    var editedBy: String = "",
    val hideList: List<String> = listOf(),
    val classCode: String = AppPreferences.userClassCode,
    val createTimestamp: Long = System.currentTimeMillis() / 1000,
    val id: String = (createTimestamp * endTimestamp).toString(),
) : Parcelable
