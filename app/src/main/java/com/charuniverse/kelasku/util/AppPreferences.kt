package com.charuniverse.kelasku.util

import android.content.Context
import android.content.SharedPreferences
import com.charuniverse.kelasku.data.models.User

object AppPreferences {

    private const val NAME = "Kelas Kita"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    //SharedPreferences variables
    private val mUserEmail = Pair("userEmail", "")
    private val mUserClassName = Pair("userClassName", "")
    private val mUserClassCode = Pair("userClassCode", "")
    private val mIsUserAdmin = Pair("isUserAdmin", false)

    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    //an inline function to put variable and save it
    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    //SharedPreferences variables getters/setters
    var userEmail: String
        get() = preferences.getString(mUserEmail.first, mUserEmail.second) ?: ""
        set(value) = preferences.edit {
            it.putString(mUserEmail.first, value)
        }

    var userClassName: String
        get() = preferences.getString(mUserClassName.first, mUserClassName.second) ?: ""
        set(value) = preferences.edit {
            it.putString(mUserClassName.first, value)
        }

    var userClassCode: String
        get() = preferences.getString(mUserClassCode.first, mUserClassCode.second) ?: ""
        set(value) = preferences.edit {
            it.putString(mUserClassCode.first, value)
        }

    var isUserAdmin: Boolean
        get() = preferences.getBoolean(mIsUserAdmin.first, mIsUserAdmin.second)
        set(value) = preferences.edit {
            it.putBoolean(mIsUserAdmin.first, value)
        }

    fun hasUserInfo(): Boolean {
        return userEmail.isNotBlank() &&
                userClassName.isNotBlank() &&
                userClassCode.isNotBlank()
    }

    fun getUserInfo(): User {
        return User(
            userEmail, userClassName,
            userClassCode, isUserAdmin
        )
    }

    fun saveUserInfo(user: User) {
        userEmail = user.email
        userClassName = user.className
        userClassCode = user.classCode
        isUserAdmin = user.admin
    }

    fun deleteUserInfo() {
        userEmail = ""
        userClassName = ""
        userClassCode = ""
        isUserAdmin = false
    }
}