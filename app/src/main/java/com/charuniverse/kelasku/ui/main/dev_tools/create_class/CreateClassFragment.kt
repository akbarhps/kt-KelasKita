package com.charuniverse.kelasku.ui.main.dev_tools.create_class

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.data.models.Class
import com.charuniverse.kelasku.ui.main.MainActivity
import com.charuniverse.kelasku.util.AppPreferences
import com.charuniverse.kelasku.util.helper.SnackBarBuilder
import kotlinx.android.synthetic.main.fragment_create_class.*

class CreateClassFragment : Fragment(R.layout.fragment_create_class) {

    private lateinit var viewModel: CreateClassViewModel

    private lateinit var snackBar: SnackBarBuilder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toggleNavigationBar(false)

        snackBar = SnackBarBuilder(view)
        viewModel = ViewModelProvider(requireActivity())
            .get(CreateClassViewModel::class.java)

        uiHandler()
        uiEventListener()
    }

    private fun uiHandler() {
        if (!AppPreferences.isDeveloper) {
            findNavController().navigateUp()
        }

        cvCreateClass.setOnClickListener {
            hideKeyboard()
            val name = etCreateClassName.text.toString()
            val code = etCreateClassCode.text.toString()
            val endpoint = etCreateClassEndpoint.text.toString()
            val token = etCreateClassToken.text.toString()

            viewModel.createClass(Class(name, code, endpoint, token))
        }
    }

    private fun uiEventListener() {
        viewModel.events.observe(viewLifecycleOwner, {
            when (it) {
                is CreateClassViewModel.UIEvents.Idle ->
                    toggleProgressBar(false)
                is CreateClassViewModel.UIEvents.Loading ->
                    toggleProgressBar(true)
                is CreateClassViewModel.UIEvents.Complete -> {
                    findNavController().navigateUp()
                    viewModel.setEventToIdle()
                }
                is CreateClassViewModel.UIEvents.Error -> {
                    snackBar.short(it.error)
                    viewModel.setEventToIdle()
                }
            }
        })
    }

    private fun toggleProgressBar(show: Boolean) {
        if (show) {
            cvCreateClass.isEnabled = false
            llCreateClassProgress.visibility = View.VISIBLE
        } else {
            cvCreateClass.isEnabled = true
            llCreateClassProgress.visibility = View.GONE
        }
    }

    private fun toggleNavigationBar(show: Boolean) {
        (activity as MainActivity).toggleNavigationBar(show)
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

    override fun onDetach() {
        hideKeyboard()
        super.onDetach()
    }
}