package com.charuniverse.kelasku.ui.login.on_board

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.charuniverse.kelasku.R
import kotlinx.android.synthetic.main.fragment_on_board.*

class OnBoardFragment : Fragment(R.layout.fragment_on_board) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        cvGoToLoginPage.setOnClickListener {
//            val destination = OnBoardFragmentDirections
//                .actionOnBoardFragmentToSignInFragment()
//            findNavController().navigate(destination)
//        }
    }
}