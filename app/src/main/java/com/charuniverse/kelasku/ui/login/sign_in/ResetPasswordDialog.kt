package com.charuniverse.kelasku.ui.login.sign_in

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.util.firebase.authentication.AuthenticationUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_reset_password.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResetPasswordDialog : BottomSheetDialogFragment() {

    private var requestSent = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_reset_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListener()
    }

    private fun clickListener() {
        if (requestSent) {
            showCheckEmail()
        }

        cvResetPasswordDialog.setOnClickListener {
            hideError()
            hideKeyboard()

            val email = etResetPasswordDialogEmail.text.toString()
            llResetPasswordDialogProgress.visibility = View.VISIBLE
            requestResetPassword(email)
        }
    }

    private fun requestResetPassword(email: String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            AuthenticationUtil.resetPassword(email)
            withContext(Dispatchers.Main) {
                llResetPasswordDialogProgress.visibility = View.GONE
                showCheckEmail()
                requestSent = true
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                llResetPasswordDialogProgress.visibility = View.GONE
                showError(e.message.toString())
            }
            Log.e("ResetPasswordLog", "requestResetPassword: ${e.message}", e)
        }
    }

    private fun showCheckEmail() {
        tvResetPasswordDialogBtnText.text = "Silahkan Periksa Email Anda"
        cvResetPasswordDialog.isEnabled = false
    }

    private fun hideError() {
        tilResetPasswordDialogEmail.error = ""
        tilResetPasswordDialogEmail.isErrorEnabled = false
    }

    private fun showError(message: String) {
        tilResetPasswordDialogEmail.isErrorEnabled = true
        tilResetPasswordDialogEmail.error = message
    }

    private fun hideKeyboard() {
        requireActivity().hideKeyboard()
    }

    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(requireContext()))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}