package com.wargon.onlypassword.data

import kotlinx.coroutines.flow.Flow

interface PasswordRepository {

    fun getAllPasswordsStream(): Flow<List<Password>>

    fun getPassword(id: Long): Flow<Password>

    fun getPasswordByName(name: String): Flow<Password>

    suspend fun insertPassword(password: Password): Long

    suspend fun deletePassword(password: Password)

    suspend fun updatePassword(password: Password)
}