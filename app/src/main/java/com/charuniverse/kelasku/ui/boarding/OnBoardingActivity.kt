package com.charuniverse.kelasku.ui.boarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.ui.login.LoginActivity
import com.charuniverse.kelasku.ui.main.MainActivity
import com.charuniverse.kelasku.util.firebase.authentication.AuthenticationUtil
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_on_boarding.*

class OnBoardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

        if (AuthenticationUtil.isUserSignedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        vpOnBoarding.adapter = OnBoardingAdapter(this, 3)
        TabLayoutMediator(tlOnBoarding, vpOnBoarding) { tab, _ ->
            vpOnBoarding.setCurrentItem(tab.position, true)
        }.attach()

        cvOpenLoginActivity.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}