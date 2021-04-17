package com.charuniverse.kelasku.ui.main.dev_tools.edit_user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.data.models.User
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_edit_user.*

class EditUserDialog(
    private val user: User,
    private val dialogEvents: EditUserDialogEvents
) : BottomSheetDialogFragment() {

    interface EditUserDialogEvents {
        fun onMenuEditClick(user: User, isAdmin: Boolean)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_edit_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uiHandler()
        clickHandler()
    }

    private fun uiHandler() {
        tvDialogEditUserEmail.text = user.email
        cbDialogEditUserAdmin.isChecked = user.admin
    }

    private fun clickHandler() {
        cbDialogEditUserAdmin.setOnClickListener {
            val cbCheck = cbDialogEditUserAdmin.isChecked
            cvDialogEditUserConfirmAdmin.visibility = if (cbCheck != user.admin) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        cvDialogEditUserConfirmAdmin.setOnClickListener {
            val isAdmin = cbDialogEditUserAdmin.isChecked
            dialogEvents.onMenuEditClick(user, isAdmin)
            dismiss()
        }
    }
}