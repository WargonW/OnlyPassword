package com.wargon.onlypassword.data

import com.wargon.onlypassword.encrypt.TinkEncryptionHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.Collator
import java.util.Locale

class LocalPasswordRepository(private val passwordDao: PasswordDao) : PasswordRepository {

    val collator = Collator.getInstance(Locale.CHINA)
    override fun getAllPasswordsStream(): Flow<List<Password>> {
        return passwordDao.getAllPasswords()
            .map { pwdList ->
                pwdList.map{ pwd ->
                    decryptPwd(pwd)
                }.sortedWith(compareBy(collator) { it.name })
            }
    }

    override fun getPassword(id: Long): Flow<Password> {
        return passwordDao.getPassword(id)
            .map { pwd ->
                decryptPwd(pwd)
            }
    }

    override fun getPasswordByName(name: String): Flow<Password> {
        return passwordDao.getPasswordByName(name)
            .map {pwd ->
                decryptPwd(pwd)
            }
    }

    override suspend fun insertPassword(password: Password): Long {
        val encryptPassword = encryptPwd(password);
        return passwordDao.insert(encryptPassword)
    }

    override suspend fun deletePassword(password: Password) {
        passwordDao.delete(password)
    }

    override suspend fun updatePassword(password: Password) {
        passwordDao.update(encryptPwd(password))
    }

    private fun encryptPwd(password: Password): Password {
        val rawPassword = password.password;
        val encryptPassword = TinkEncryptionHelper.encryptToString(rawPassword);
        return password.copy(password = encryptPassword)
    }

    private fun decryptPwd(password: Password): Password {
        try {
            val encryptPwd = password.password
            val decryptPwd = TinkEncryptionHelper.decryptFromString(encryptPwd)
            return password.copy(password = decryptPwd)
        }catch (e: Exception) {
            return password
        }
    }
}