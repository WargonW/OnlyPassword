package com.wargon.onlypassword.ui

import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wargon.onlypassword.OnlyPasswordApplication
import com.wargon.onlypassword.data.EncryptPasswordData
import com.wargon.onlypassword.data.NAME_MAX_LENGTH
import com.wargon.onlypassword.data.NOTE_MAX_LENGTH
import com.wargon.onlypassword.data.PASSWORD_MAX_LENGTH
import com.wargon.onlypassword.data.Password
import com.wargon.onlypassword.data.PasswordRepository
import com.wargon.onlypassword.data.USERNAME_MAX_LENGTH
import com.wargon.onlypassword.encrypt.TinkEncryptionHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import java.sql.SQLException

private const val TIMEOUT_MILLIS = 5_000L


class AppViewModel(private val passwordRepository: PasswordRepository) : ViewModel() {

    var dbError = MutableStateFlow<String?>(null)
        private set

    var searchKeyword = MutableStateFlow<String?>(null)
        private set

    var snackBarState = MutableStateFlow<SnackBarState>(SnackBarState())
        private set

    val pwdListStateFlow: StateFlow<List<Password>> = passwordRepository.getAllPasswordsStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = listOf()
        )

    var hasMasterPwd = MutableStateFlow<Boolean>(false)
        private set


    suspend fun savePwdInfo(password: Password): Long? {
        try {
            if(validatePwdInfo(password)) {
                return passwordRepository.insertPassword(password);
            }
        }catch (e: SQLiteException) {
            dbError.value = e.message
        }
        return null
    }

    suspend fun updatePwdInfo(password: Password): Boolean {
        try {
            if(validatePwdInfo(password)) {
                passwordRepository.updatePassword(password)
                return true
            }
        }catch (e: SQLException){
            dbError.value = e.message
        }
        return false
    }

    fun validatePwdInfo(password: Password): Boolean {
        return with(password) {
            name.isNotBlank() && username.isNotBlank() && password.password.isNotBlank()
                    &&
                    name.length <= NAME_MAX_LENGTH &&
                    username.length <= USERNAME_MAX_LENGTH &&
                    password.password.length <= PASSWORD_MAX_LENGTH
                    && (note == null || note.length <= NOTE_MAX_LENGTH)
        }
    }

    suspend fun checkDuplicateName(name: String): Boolean {
        return passwordRepository.getPasswordByName(name).firstOrNull() != null
    }

    fun updateSearchKeyword(keyword: String?) {
        searchKeyword.value = keyword
    }

    suspend fun getPwdInfo(id: Long): Password? {
        return passwordRepository.getPassword(id).firstOrNull()
    }

    fun updateSnackBar(show: Boolean, info: String) {
        snackBarState.value = SnackBarState(show,info)
    }

    suspend fun deletePwdInfo(password: Password): Boolean {
        try {
            passwordRepository.deletePassword(password)
        }catch (e: SQLException) {
            dbError.value = e.message
            return false
        }
        return true
    }

    fun checkHasMasterPwd() {
        hasMasterPwd.value = TinkEncryptionHelper.hasMasterPassword(OnlyPasswordApplication.getAppContext())
    }

    fun setMasterPwd(password: String) {
        TinkEncryptionHelper.storeMasterPassword(password, OnlyPasswordApplication.getAppContext())
        checkHasMasterPwd()
    }

    fun validateMasterPwd(password: String): Boolean {
        return TinkEncryptionHelper.verifyMasterPassword(password, OnlyPasswordApplication.getAppContext())
    }

    fun generateEncryptPwdInfo(): EncryptPasswordData? {
        val passwordList = pwdListStateFlow.value
        val saltString = TinkEncryptionHelper.generateSaltString()
        val timeStamp = System.currentTimeMillis()
        val masterPwd = TinkEncryptionHelper.getMasterPwd(OnlyPasswordApplication.getAppContext())
        if(masterPwd == null) {
            throw RuntimeException("主密码不存在")
        }
        val encryptPasswordList = passwordList.map { password ->
            val rawUsername = password.username
            val rawPassword = password.password
            val encryptUsername = TinkEncryptionHelper.encryptTextByPassword(rawUsername,masterPwd,saltString,timeStamp)
            val encryptPassword = TinkEncryptionHelper.encryptTextByPassword(rawPassword,masterPwd,saltString,timeStamp)
            password.copy(username = encryptUsername, password = encryptPassword)
        }
        return EncryptPasswordData(
            saltString = saltString,
            timeStamp = timeStamp,
            passwordList = encryptPasswordList
        )
    }

    suspend fun importEncryptPwdInfo(data: EncryptPasswordData): Boolean {
        try {
            val masterPwd = TinkEncryptionHelper.getMasterPwd(OnlyPasswordApplication.getAppContext())
            if(masterPwd == null) {
                throw RuntimeException("主密码不存在")
            }
            val saltString = data.saltString
            val timeStamp = data.timeStamp
            val passwordList = data.passwordList

            val rawPasswordList = passwordList.sortedBy { password -> password.id }.map { password ->
                val encryptUsername = password.username
                val rawUsername = TinkEncryptionHelper.decryptTextByPassword(encryptUsername,saltString,timeStamp,masterPwd)
                val encryptPassword = password.password
                var rawPassword = TinkEncryptionHelper.decryptTextByPassword(encryptPassword,saltString,timeStamp,masterPwd)
                password.copy(username = rawUsername, password = rawPassword, id = 0L)
            }
            rawPasswordList.forEach { ele ->
                passwordRepository.insertPassword(ele)
            }
            return true
        }catch (e: Exception) {
            return false
        }

    }

}


data class SnackBarState(
    val show: Boolean = false,
    val info: String = ""
)