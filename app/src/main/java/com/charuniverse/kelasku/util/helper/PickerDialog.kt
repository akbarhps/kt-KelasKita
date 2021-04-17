package com.charuniverse.kelasku.util.helper

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import java.util.*

object PickerDialog {

    fun buildTimePickerDialog(
        context: Context,
        listener: TimePickerDialog.OnTimeSetListener
    ): TimePickerDialog {
        return TimePickerDialog(
            context, listener,
            8, 0, true
        )
    }

    fun buildDatePickerDialog(
        context: Context,
        listener: DatePickerDialog.OnDateSetListener
    ): DatePickerDialog {
        return Calendar.getInstance().let {
            val year = it.get(Calendar.YEAR)
            val month = it.get(Calendar.MONTH)
            val day = it.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(
                context, listener,
                year, month, day
            ).apply {
                datePicker.minDate = System.currentTimeMillis() - 1000
            }
        }
    }

}