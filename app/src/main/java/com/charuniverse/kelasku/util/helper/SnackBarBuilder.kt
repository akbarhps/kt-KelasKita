package com.charuniverse.kelasku.util.helper

import android.view.View
import com.google.android.material.snackbar.Snackbar

class SnackBarBuilder(private val view: View) {

    fun short(message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
            .show()
    }

    fun long(message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .show()
    }

    fun actionShort(message: String, buttonText: String, operation: () -> Unit) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
            .setAction(buttonText) { operation() }
            .show()
    }

    fun actionLong(message: String, buttonText: String, operation: () -> Unit) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAction(buttonText) { operation() }
            .show()
    }

}