package com.charuniverse.kelasku.ui.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.charuniverse.kelasku.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_profile_menu.*

class ProfileMenuDialog(
    private val menuEvents: ProfileDialogMenuEvents
) : BottomSheetDialogFragment() {

    interface ProfileDialogMenuEvents {
        fun onMenuRefreshClick()
        fun onMenuLogOutClick()
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
        return inflater.inflate(R.layout.dialog_profile_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clickHandler()
    }

    private fun clickHandler() {
        var isConfirmShow = false

        cvProfileDialogRefresh.setOnClickListener {
            menuEvents.onMenuRefreshClick()
            dismiss()
        }

        cvProfileDialogLogOut.setOnClickListener {
            isConfirmShow = if (isConfirmShow) {
                cvProfileDialogConfirmLogOut.visibility = View.GONE
                cvProfileDialogConfirmLogOut.startAnimation(hideConfirmAnimation)
                false
            } else {
                cvProfileDialogConfirmLogOut.visibility = View.VISIBLE
                cvProfileDialogConfirmLogOut.startAnimation(showConfirmAnimation)
                true
            }
        }

        cvProfileDialogConfirmLogOut.setOnClickListener {
            menuEvents.onMenuLogOutClick()
            dismiss()
        }
    }

}