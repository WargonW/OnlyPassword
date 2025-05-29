package com.wargon.onlypassword.ui

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import com.wargon.onlypassword.OnlyPasswordApplication
import kotlinx.coroutines.flow.MutableStateFlow

class AuthViewModel : ViewModel() {

    private val lastAuthTime = MutableStateFlow<Long>(0)
    val needAuth = MutableStateFlow<Boolean>(true)

    private val lastImportStartTime = MutableStateFlow<Long>(0)


    fun onStart() {
        if(needAuth.value) {
            return
        }
        if (lastAuthTime.value + 60 * 1000 <= System.currentTimeMillis()) {
            needAuth.value = true
        }
        //给import 10分钟的容错时间
        if (lastImportStartTime.value + 10 * 60 * 1000 > System.currentTimeMillis()) {
            needAuth.value = false
        }
        lastImportStartTime.value = 0
    }
    fun authOk() {
        needAuth.value = false
        lastAuthTime.value = System.currentTimeMillis()
    }

    fun importStart() {
        lastImportStartTime.value = System.currentTimeMillis()
    }

    var lastLockTime = MutableStateFlow<Long>(0L)
        private set
    var loginFailedTimes = MutableStateFlow<Long>(0L)
        private set


    val commonSharedPreference = "only_password_common_prefs"
    val lastLoginLockTime = "last_login_lock_time"
    val loginFailedTimesKey = "login_failed_Times"
    val maxLoginFailedTimes = 10
    val loginFailedLockMills = 60 * 60 * 1000


    private fun getLastLoginLockTime(): Long {
        val sharedPreferences = OnlyPasswordApplication.getAppContext().getSharedPreferences(commonSharedPreference,
            Context.MODE_PRIVATE)
        val lastLoginLockTime = sharedPreferences.getLong(lastLoginLockTime,1)
        return lastLoginLockTime
    }

    private fun getLoginFailedTimes(): Long {
        val sharedPreferences = OnlyPasswordApplication.getAppContext().getSharedPreferences(commonSharedPreference,
            Context.MODE_PRIVATE)
        val lastLoginLockTime = sharedPreferences.getLong(loginFailedTimesKey,0)
        return lastLoginLockTime
    }

    fun loginFailedOnce() {
        val sharedPreferences = OnlyPasswordApplication.getAppContext().getSharedPreferences(commonSharedPreference,
            Context.MODE_PRIVATE)
        val failedTimes = getLoginFailedTimes() + 1
        sharedPreferences.edit {
            putLong(loginFailedTimesKey, failedTimes)
            commit()
        }
        loginFailedTimes.value = failedTimes
        if (loginFailedTimes.value >= maxLoginFailedTimes) {
            val now = System.currentTimeMillis()
            sharedPreferences.edit{
                putLong(lastLoginLockTime, now)
                commit()
            }
            lastLockTime.value = now
        }
    }

    fun clearLoginFailedInfo() {
        lastLockTime.value = 0
        val sharedPreferences = OnlyPasswordApplication.getAppContext().getSharedPreferences(commonSharedPreference,
            Context.MODE_PRIVATE)
        sharedPreferences.edit {
            putLong(loginFailedTimesKey, 0)
            putLong(lastLoginLockTime,1)
        }
    }

    fun refreshLoginState() {
        val lockTime = getLastLoginLockTime()
        val failedTimes = getLoginFailedTimes()
        lastLockTime.value = lockTime
        loginFailedTimes.value = failedTimes
    }
}