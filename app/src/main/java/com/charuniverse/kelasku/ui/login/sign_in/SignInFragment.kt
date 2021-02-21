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
import com.charuniverse.kelasku.ui.main.MainActivity
import com.charuniverse.kelasku.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_sign_in.*

class SignInFragment : Fragment(R.layout.fragment_sign_in) {

    private lateinit var baseView: View
    private lateinit var viewModel: SignInViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        baseView = view
        viewModel = ViewModelProvider(requireActivity())
            .get(SignInViewModel::class.java)

        uiListener()
        eventsListener()
    }

    private fun uiListener() {
        cvSignIn.setOnClickListener {
            hideKeyboard()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            viewModel.signIn(email, password)
        }
        cvOpenSignUpPage.setOnClickListener {
            findNavController()
                .navigate(R.id.action_signInFragment_to_signUpFragment)
        }
    }

    private fun eventsListener() {
        viewModel.events.observe(viewLifecycleOwner, {
            when (it) {
                is SignInViewModel.UIEvents.Loading -> {
                    toggleProgressBar(true)
                }
                is SignInViewModel.UIEvents.Success -> {
                    updateUi()
                }
                is SignInViewModel.UIEvents.Error -> {
                    buildSnackBar(it.error)
                    toggleProgressBar(false)
                }
                else -> Unit
            }
        })
    }

    private fun toggleProgressBar(show : Boolean) {
        if (show) {
            cvSignIn.isEnabled = false
            llSignInLoading.visibility = View.VISIBLE
            llSignInLogin.visibility = View.GONE
        } else {
            cvSignIn.isEnabled = true
            llSignInLoading.visibility = View.GONE
            llSignInLogin.visibility = View.VISIBLE
        }
    }

    private fun updateUi() {
        requireActivity().let {
            it.startActivity(Intent(it, MainActivity::class.java))
            it.finish()
        }
    }

    private fun buildSnackBar(message: String) {
        Snackbar.make(baseView, message, Snackbar.LENGTH_LONG)
            .show()
    }

    private fun hideKeyboard() {
        requireActivity().hideKeyboard()
    }

    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(requireContext()))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}