package com.charuniverse.kelasku.util.helper

import android.annotation.SuppressLint
import android.text.Html
import java.text.SimpleDateFormat
import java.util.*

object Converter {

    @SuppressLint("SimpleDateFormat")
    fun convertLongToDate(time: Long): String {
        val date = Date(time * 1000)
        return SimpleDateFormat("dd/MM/yyy HH:mm").format(date)
    }

    fun convertDescription(description: String): String {
        return if (hasHTMLTag(description)) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT).toString()
            } else {
                Html.fromHtml(description).toString()
            }
        } else {
            description
        }
    }

    private fun hasHTMLTag(text: String): Boolean {
        return "<[^>]*>".toRegex().containsMatchIn(text)
    }

}