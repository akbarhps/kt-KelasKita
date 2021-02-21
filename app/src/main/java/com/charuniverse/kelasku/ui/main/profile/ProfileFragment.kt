package com.charuniverse.kelasku.ui.main.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.data.models.User
import com.charuniverse.kelasku.ui.login.LoginActivity
import com.charuniverse.kelasku.util.AppPreferences
import com.charuniverse.kelasku.util.firebase.authentication.AuthenticationUtil
import com.charuniverse.kelasku.util.firebase.messaging.MessagingUtil
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var user: User

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = AppPreferences.getUserInfo()

        setupUI()
        clickListener()
    }

    private fun setupUI() {
        tvProfileEmail.text = user.email
        tvProfileClass.text = "\uD83C\uDFDB " + user.className
    }

    private fun clickListener() {
        cvProfileLogOut.setOnClickListener { view ->
            Snackbar.make(view, "Apakah anda yakin ingin Log Out?", Snackbar.LENGTH_SHORT)
                .setAction("Yakin") {
                    unsubscribeTopic(view)
                }.show()
        }
    }

    private fun unsubscribeTopic(view: View) = CoroutineScope(Dispatchers.Main).launch {
        try {
            MessagingUtil.unsubscribeToTopic(AppPreferences.userClassCode)
            AuthenticationUtil.logout()
            updateUI()
        } catch (e: Exception) {
            Snackbar.make(view, e.message.toString(), Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun updateUI() {
        requireActivity().let {
            it.startActivity(Intent(it, LoginActivity::class.java))
            it.finish()
        }
    }
}