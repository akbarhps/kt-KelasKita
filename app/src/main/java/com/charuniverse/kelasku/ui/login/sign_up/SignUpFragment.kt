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
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.data.models.Class
import com.charuniverse.kelasku.ui.main.MainActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_sign_up.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpFragment : Fragment(R.layout.fragment_sign_up) {

    private lateinit var baseView: View
    private var classInfo: Class? = null
    private lateinit var viewModel: SignUpViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        baseView = view
        viewModel = ViewModelProvider(requireActivity())
            .get(SignUpViewModel::class.java)

        eventListener()
        classesListener()
        buttonClickListener()
    }

    private fun eventListener() {
        viewModel.events.observe(viewLifecycleOwner, {
            when (it) {
                is SignUpViewModel.UIEvents.NoData -> {
                    toggleSpinnerProgressBar("ERROR")
                }
                is SignUpViewModel.UIEvents.Loading -> {
                    toggleButtonProgressBar(true)
                }
                is SignUpViewModel.UIEvents.Success -> {
                    updateUi()
                }
                is SignUpViewModel.UIEvents.Error -> {
                    buildSnackBar(it.error)
                    toggleButtonProgressBar(false)
                }
                else -> Unit
            }
        })
    }

    private fun classesListener() = CoroutineScope(Dispatchers.Main).launch {
        viewModel.classes.observe(viewLifecycleOwner, {
            toggleSpinnerProgressBar("SUCCESS")
            val className = viewModel.getClassName()
            setupSpinner(className)
        })
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

    private fun buttonClickListener() {
        cvSignUp.setOnClickListener {
            hideKeyboard()
            if (classInfo == null) {
                buildSnackBar("Anda belum memilih kelas 🙄")
                return@setOnClickListener
            }
            val email = etSignUpEmail.text.toString()
            val password = etSignUpPassword.text.toString()
            viewModel.signUp(email, password, classInfo!!.name, classInfo!!.code)
        }
        cvOpenSignInPage.setOnClickListener {
            requireActivity().onBackPressed()
        }
        ivRefreshAvailableClass.setOnClickListener {
            startRefreshAnimation(it)
            if (classInfo == null) {
                toggleSpinnerProgressBar("LOADING")
                viewModel.getAvailableClass()
            }
        }
        tvSignUpNoClass.setOnClickListener {
            DialogNoClass().show(parentFragmentManager, null)
        }
    }

    private fun toggleSpinnerProgressBar(state: String) {
        when (state) {
            "SUCCESS" -> {
                spinnerAvailableClass.visibility = View.VISIBLE
                progressAvailableClass.visibility = View.GONE
                tvAvailableClassError.visibility = View.GONE
            }
            "LOADING" -> {
                spinnerAvailableClass.visibility = View.GONE
                progressAvailableClass.visibility = View.VISIBLE
                tvAvailableClassError.visibility = View.GONE
            }
            else -> {
                spinnerAvailableClass.visibility = View.GONE
                progressAvailableClass.visibility = View.GONE
                tvAvailableClassError.visibility = View.VISIBLE
            }
        }
    }

    private fun toggleButtonProgressBar(show: Boolean) {
        if (show) {
            cvSignUp.isEnabled = false
            llSignUpLoading.visibility = View.VISIBLE
            llSignUpRegister.visibility = View.GONE
        } else {
            cvSignUp.isEnabled = true
            llSignUpLoading.visibility = View.GONE
            llSignUpRegister.visibility = View.VISIBLE
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
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}