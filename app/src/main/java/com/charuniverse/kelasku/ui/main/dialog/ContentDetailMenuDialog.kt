package com.charuniverse.kelasku.ui.main.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.util.helper.ContentPermission
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_content_detail_menu.*

class ContentDetailMenuDialog(
    private val permission: ContentPermission,
    private val dialogEvents: DetailMenuDialogEvents
) : BottomSheetDialogFragment() {

    interface DetailMenuDialogEvents {
        fun onMenuShareClick()
        fun onMenuHideClick()
        fun onMenuEditClick()
        fun onMenuDeleteClick()
    }

    private lateinit var showConfirmAnimation: Animation
    private lateinit var hideConfirmAnimation: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)

        showConfirmAnimation = AnimationUtils.loadAnimation(
            requireContext(), R.anim.anim_button_confirm_show
        )

        hideConfirmAnimation = AnimationUtils.loadAnimation(
            requireContext(), R.anim.anim_button_confirm_hide
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_content_detail_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uiHandler()
        clickHandler()
    }

    private fun uiHandler() {
        val editVisibility = if (permission.EDIT) {
            View.VISIBLE
        } else {
            View.GONE
        }

        val deleteVisibility = if (permission.DELETE) {
            View.VISIBLE
        } else {
            View.GONE
        }

        cvContentDetailMenuEdit.visibility = editVisibility
        cvContentDetailMenuDelete.visibility = deleteVisibility
    }

    private fun clickHandler() {
        var confirmHideShown = false
        var confirmDeleteShown = false

        cvContentDetailMenuShare.setOnClickListener {
            dialogEvents.onMenuShareClick()
            dismiss()
        }

        cvContentDetailMenuHide.setOnClickListener {
            if (confirmDeleteShown) {
                cvContentDetailMenuConfirmDelete.visibility = View.GONE
                cvContentDetailMenuConfirmDelete.startAnimation(hideConfirmAnimation)
                confirmDeleteShown = false
            }
            confirmHideShown = if (confirmHideShown) {
                cvContentDetailMenuConfirmHide.visibility = View.GONE
                cvContentDetailMenuConfirmHide.startAnimation(hideConfirmAnimation)
                false
            } else {
                cvContentDetailMenuConfirmHide.visibility = View.VISIBLE
                cvContentDetailMenuConfirmHide.startAnimation(showConfirmAnimation)
                true
            }
        }

        cvContentDetailMenuConfirmHide.setOnClickListener {
            dialogEvents.onMenuHideClick()
            dismiss()
        }

        cvContentDetailMenuEdit.setOnClickListener {
            dialogEvents.onMenuEditClick()
            dismiss()
        }

        cvContentDetailMenuDelete.setOnClickListener {
            if (confirmHideShown) {
                cvContentDetailMenuConfirmHide.visibility = View.GONE
                cvContentDetailMenuConfirmHide.startAnimation(hideConfirmAnimation)
                confirmHideShown = false
            }
            confirmDeleteShown = if (confirmDeleteShown) {
                cvContentDetailMenuConfirmDelete.visibility = View.GONE
                cvContentDetailMenuConfirmDelete.startAnimation(hideConfirmAnimation)
                false
            } else {
                cvContentDetailMenuConfirmDelete.visibility = View.VISIBLE
                cvContentDetailMenuConfirmDelete.startAnimation(showConfirmAnimation)
                true
            }
        }

        cvContentDetailMenuConfirmDelete.setOnClickListener {
            dialogEvents.onMenuDeleteClick()
            dismiss()
        }
    }

}