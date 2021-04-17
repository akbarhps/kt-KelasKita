package com.charuniverse.kelasku.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.ui.login.LoginActivity
import com.charuniverse.kelasku.util.Globals
import com.charuniverse.kelasku.util.firebase.authentication.AuthenticationUtil
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Globals.refreshAssignment = true
        Globals.refreshAnnouncement = true

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.mainHostFragment) as NavHostFragment

        navController = navHostFragment.findNavController()

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.announcementFragment,
                R.id.assignmentFragment,
                R.id.profileFragment
            )
        )

        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        mainBottomNav.apply {
            setupWithNavController(navController)
            setOnNavigationItemReselectedListener {}
        }

        navController.addOnDestinationChangedListener { _, _, _ ->
            if (!AuthenticationUtil.isUserSignedIn()) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    fun toggleNavigationBar(show: Boolean) {
        val visibility = if (show) {
            View.VISIBLE
        } else {
            View.GONE
        }
        bottomNavDivider.visibility = visibility
        mainBottomNav.visibility = visibility
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navController.handleDeepLink(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}