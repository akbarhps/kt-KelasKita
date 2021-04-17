package com.charuniverse.kelasku.ui.main.dev_tools

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.charuniverse.kelasku.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_dev_tools.*

class DevToolsMenuDialog(
    private val events: DevToolsMenuEvents
) : BottomSheetDialogFragment() {

    interface DevToolsMenuEvents {
        fun onMenuAddClassClick()
        fun onMenuEditUsersClick()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_dev_tools, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickHandler()
    }

    private fun clickHandler() {
        cvDevToolsMenuAddClass.setOnClickListener {
            events.onMenuAddClassClick()
            dismiss()
        }

        cvDevToolsMenuEditUsers.setOnClickListener {
            events.onMenuEditUsersClick()
            dismiss()
        }
    }

}