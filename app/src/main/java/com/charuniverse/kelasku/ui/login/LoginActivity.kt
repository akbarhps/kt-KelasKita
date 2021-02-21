package com.charuniverse.kelasku.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.charuniverse.kelasku.ui.main.MainActivity
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.util.AppPreferences

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (AppPreferences.hasUserInfo()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}