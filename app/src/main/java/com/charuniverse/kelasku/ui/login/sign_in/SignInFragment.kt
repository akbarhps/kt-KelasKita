package com.charuniverse.kelasku.ui.login.sign_in

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.ui.main.MainActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_sign_in.*

class SignInFragment : Fragment(R.layout.fragment_sign_in) {

    private val dialog = ResetPasswordDialog()
    private lateinit var viewModel: SignInViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())
            .get(SignInViewModel::class.java)

        uiEventsListener()
        uiManager()
    }

    private fun uiEventsListener() {
        viewModel.events.observe(viewLifecycleOwner, {
            when (it) {
                is SignInViewModel.UIEvents.Idle -> Unit
                is SignInViewModel.UIEvents.Loading -> toggleProgressBar(true)
                is SignInViewModel.UIEvents.Success -> updateUi()
                is SignInViewModel.UIEvents.Error -> {
                    showError(it.error)
                    toggleProgressBar(false)
                }
            }
        })
    }

    private fun showError(error: String) {
        val emailError = error.contains("email", true) ||
                error.contains("record", true)
        val passwordError = error.contains("password", true)

        if (emailError || etSignInEmail.text.toString().isEmpty()) {
            tilSignInEmail.error = error
            tilSignInEmail.isErrorEnabled = true
        } else if (passwordError || etSignInPassword.text.toString().isEmpty()) {
            tilSignInPassword.error = error
            tilSignInPassword.isErrorEnabled = true
        } else {
            buildSnackBar(error)
        }
    }

    private fun hideAllError() {
        tilSignInPassword.error = ""
        tilSignInPassword.isErrorEnabled = false
        tilSignInEmail.error = ""
        tilSignInEmail.isErrorEnabled = false
    }

    private fun uiManager() {
        cvSignIn.setOnClickListener {
            hideKeyboard()
            hideAllError()

            val email = etSignInEmail.text.toString()
            val password = etSignInPassword.text.toString()
            viewModel.signIn(email, password)
        }

        cvOpenSignUpPage.setOnClickListener {
            val destination = SignInFragmentDirections
                .actionSignInFragmentToSignUpFragment()
            findNavController().navigate(destination)
        }

        tvSignInForgotPassword.setOnClickListener {
            dialog.show(parentFragmentManager, null)
        }
    }

    private fun toggleProgressBar(show: Boolean) {
        if (show) {
            cvSignIn.isEnabled = false
            llSignInProgress.visibility = View.VISIBLE
        } else {
            cvSignIn.isEnabled = true
            llSignInProgress.visibility = View.GONE
        }
    }

    private fun updateUi() {
        requireActivity().let {
            it.startActivity(Intent(it, MainActivity::class.java))
            it.finish()
        }
    }

    private fun buildSnackBar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
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