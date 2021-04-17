package com.charuniverse.kelasku.ui.login.sign_up

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.data.models.Class
import com.charuniverse.kelasku.ui.main.MainActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_sign_up.*

class SignUpFragment : Fragment(R.layout.fragment_sign_up) {

    enum class SpinnerEvents {
        SUCCESS,
        LOADING,
        ERROR
    }

    private var classInfo: Class? = null
    private lateinit var viewModel: SignUpViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())
            .get(SignUpViewModel::class.java)

        uiEventsListener()
        spinnerEventsListener()
        uiManager()
    }

    private fun spinnerEventsListener() {
        viewModel.spinnerEvents.observe(viewLifecycleOwner, {
            when (it) {
                is SignUpViewModel.SpinnerEvents.Idle -> Unit
                is SignUpViewModel.SpinnerEvents.Loading ->
                    toggleSpinnerProgressBar(SpinnerEvents.LOADING)
                is SignUpViewModel.SpinnerEvents.Error ->
                    toggleSpinnerProgressBar(SpinnerEvents.ERROR)
            }
        })
    }

    private fun uiEventsListener() {
        viewModel.events.observe(viewLifecycleOwner, {
            when (it) {
                is SignUpViewModel.UIEvents.Idle -> Unit
                is SignUpViewModel.UIEvents.Success -> updateUi()
                is SignUpViewModel.UIEvents.Loading ->
                    toggleButtonProgressBar(true)
                is SignUpViewModel.UIEvents.Error -> {
                    showError(it.error)
                    toggleButtonProgressBar(false)
                    viewModel.setEventToIdle()
                }
            }
        })
    }

    private fun showError(error: String) {
        val emailError = error.contains("email", true)
        val passwordError = error.contains("password", true)

        if (emailError || etSignUpEmail.text.toString().isEmpty()) {
            tilSignUpEmail.error = error
            tilSignUpEmail.isErrorEnabled = true
        } else if (passwordError || etSignUpPassword.text.toString().isEmpty()) {
            tilSignUpPassword.error = error
            tilSignUpPassword.isErrorEnabled = true
        } else {
            buildSnackBar(error)
        }
    }

    private fun hideError() {
        tilSignUpEmail.error = ""
        tilSignUpEmail.isErrorEnabled = false
        tilSignUpPassword.error = ""
        tilSignUpPassword.isErrorEnabled = false
    }

    private fun uiManager() {
        viewModel.classes.observe(viewLifecycleOwner, {
            toggleSpinnerProgressBar(SpinnerEvents.SUCCESS)
            setupSpinner(viewModel.getClassName())
        })

        ivRefreshAvailableClass.setOnClickListener {
            startRefreshAnimation(it)
            viewModel.getAvailableClass()
        }

        cvSignUp.setOnClickListener {
            hideKeyboard()
            hideError()

            if (classInfo == null) {
                buildSnackBar("Anda belum memilih kelas 🙄")
                return@setOnClickListener
            }

            val email = etSignUpEmail.text.toString()
            val password = etSignUpPassword.text.toString()
            viewModel.signUp(email, password, classInfo!!.name, classInfo!!.code)
        }

        tvSignUpNoClass.setOnClickListener {
            RequestClassDialog().show(parentFragmentManager, null)
        }

        cvOpenSignInPage.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupSpinner(className: List<String>) {
        spinnerAvailableClass.adapter =
            ArrayAdapter(requireContext(), R.layout.spinner_text_view, className)

        spinnerAvailableClass.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                classInfo = viewModel.getClassInfoByPosition(position)
            }
        }
    }

    private fun toggleSpinnerProgressBar(event: SpinnerEvents) {
        when (event) {
            SpinnerEvents.SUCCESS -> {
                spinnerAvailableClass.visibility = View.VISIBLE
                progressAvailableClass.visibility = View.GONE
                tvAvailableClassError.visibility = View.GONE
            }
            SpinnerEvents.LOADING -> {
                spinnerAvailableClass.visibility = View.GONE
                progressAvailableClass.visibility = View.VISIBLE
                tvAvailableClassError.visibility = View.GONE
            }
            SpinnerEvents.ERROR -> {
                spinnerAvailableClass.visibility = View.GONE
                progressAvailableClass.visibility = View.GONE
                tvAvailableClassError.visibility = View.VISIBLE
            }
        }
    }

    private fun toggleButtonProgressBar(show: Boolean) {
        if (show) {
            cvSignUp.isEnabled = false
            llSignUpProgress.visibility = View.VISIBLE
        } else {
            cvSignUp.isEnabled = true
            llSignUpProgress.visibility = View.GONE
        }
    }

    private fun updateUi() {
        requireActivity().let {
            it.startActivity(Intent(it, MainActivity::class.java))
            it.finish()
        }
    }

    private fun startRefreshAnimation(view: View) {
        view.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(), R.anim.rotate_full_degree
            )
        )
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