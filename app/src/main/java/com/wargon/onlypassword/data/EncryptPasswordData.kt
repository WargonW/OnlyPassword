package com.wargon.onlypassword.data

import kotlinx.serialization.Serializable

@Serializable
data class EncryptPasswordData(
    val saltString: String,
    val timeStamp: Long,
    val passwordList: List<Password>
)
