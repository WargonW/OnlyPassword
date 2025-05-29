package com.wargon.onlypassword.encrypt

import android.content.Context
import android.util.Base64
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import com.google.crypto.tink.subtle.AesGcmJce
import java.security.GeneralSecurityException
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.spec.SecretKeySpec
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object TinkEncryptionHelper {
    private const val KEYSET_NAME = "only_password_encryption_keyset"
    private const val PREFERENCE_FILE_NAME = "only_password_secure_prefs"
    private const val MASTER_PWD_SALT_KEY = "master_password_salt"
    private const val ENCRYPTED_MASTER_PASSWORD_KEY = "encrypted_master_password"

    private lateinit var aead: Aead


    @Throws(GeneralSecurityException::class)
    fun init(context: Context) {
        AeadConfig.register()
        aead = AndroidKeysetManager.Builder()
            .withSharedPref(context, KEYSET_NAME, PREFERENCE_FILE_NAME)
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri("android-keystore://only_password_master_key")
            .build()
            .keysetHandle
            .getPrimitive(Aead::class.java)
    }

    @Throws(GeneralSecurityException::class)
    fun encrypt(plaintext: String): ByteArray {
        return aead.encrypt(plaintext.toByteArray(), null)
    }

    @Throws(GeneralSecurityException::class)
    fun encryptToString(plaintext: String): String {
        return bytesToBase64(encrypt(plaintext))
    }

    @Throws(GeneralSecurityException::class)
    fun decrypt(ciphertext: ByteArray): String {
        return String(aead.decrypt(ciphertext, null))
    }

    @Throws(GeneralSecurityException::class)
    fun decryptFromString(encryptString: String): String {
        return decrypt(base64ToBytes(encryptString))
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun getMasterPwd(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE)
        val saltHex = prefs.getString(MASTER_PWD_SALT_KEY, null) ?: return null
        val encryptedHex = prefs.getString(ENCRYPTED_MASTER_PASSWORD_KEY, null) ?: return null

        val salt = saltHex.hexToByteArray()
        val encryptedPassword = encryptedHex.hexToByteArray()
        val decrypted = aead.decrypt(encryptedPassword, salt)
        val storedPassword = String(decrypted, Charsets.UTF_8)
        return storedPassword
    }

    fun hasMasterPassword(context: Context): Boolean {
        val encryptedHex = context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE)
            .getString(ENCRYPTED_MASTER_PASSWORD_KEY, null)
        return encryptedHex != null
    }

    /*
    * 安全存储用户密码
    */
    @OptIn(ExperimentalStdlibApi::class)
    @Throws(GeneralSecurityException::class)
    fun storeMasterPassword(password: String, context: Context) {
        // 1. 生成随机盐值
        val salt = generateSalt()

        // 2. 使用AEAD加密密码（盐值作为关联数据）
        val encryptedPassword = aead.encrypt(
            password.toByteArray(Charsets.UTF_8),
            salt // 将盐值作为关联数据(AD)确保加密绑定到特定盐值
        )

        // 3. 存储盐值和加密结果
        context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE).edit()
            .putString(MASTER_PWD_SALT_KEY, salt.toHexString())
            .putString(ENCRYPTED_MASTER_PASSWORD_KEY, encryptedPassword.toHexString())
            .apply()
    }

    /**
     * 验证用户密码
     */
    @OptIn(ExperimentalStdlibApi::class)
    @Throws(GeneralSecurityException::class)
    fun verifyMasterPassword(inputPassword: String, context: Context): Boolean {
        val storedPassword = getMasterPwd(context) ?: return false

        // 尝试解密
        return try {
            secureCompare(storedPassword, inputPassword)
        } catch (e: GeneralSecurityException) {
            false // 解密失败说明密码不匹配
        }
    }

    // 安全比较防止时序攻击
    private fun secureCompare(a: String, b: String): Boolean {
        return MessageDigest.isEqual(
            a.toByteArray(Charsets.UTF_8),
            b.toByteArray(Charsets.UTF_8)
        )
    }
//
//    // 扩展函数：ByteArray转十六进制字符串
//    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
//
//    // 扩展函数：十六进制字符串转ByteArray
//    private fun String.hexToBytes(): ByteArray {
//        require(length % 2 == 0) { "Hex string must have even length" }
//        return chunked(2)
//            .map { it.toInt(16).toByte() }
//            .toByteArray()
//    }


    // 盐值应该每个用户不同且安全存储
    private const val SALT_LENGTH = 16 // 16字节盐值
    private const val ITERATIONS = 100_000 // 迭代次数(越高越安全但越慢)
    private const val KEY_LENGTH = 256

    // 从字节数组创建自定义密钥
    private fun createAeadWithCustomKey(customKeyBytes: ByteArray): Aead {
        val keySpec = SecretKeySpec(customKeyBytes, "AES")
        return AesGcmJce(keySpec.encoded) // 使用 Tink 的 JCE 实现
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun encryptTextByPassword(text: String, password: String, saltString: String, timeStamp: Long): String {
        val salt = saltString.hexToByteArray()
        val key = deriveKey(password,salt)
        val customAead = createAeadWithCustomKey(key)
        val encryptText = customAead.encrypt(text.toByteArray(Charsets.UTF_8),timeStamp.toString().toByteArray(Charsets.UTF_8))
        return encryptText.toHexString()
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun decryptTextByPassword(encryptText: String, saltString: String, timeStamp: Long, password: String): String {
        val key = deriveKey(password,saltString.hexToByteArray())
        val customAead = createAeadWithCustomKey(key)
        val plaintext = customAead.decrypt(encryptText.hexToByteArray(),timeStamp.toString().toByteArray(Charsets.UTF_8))
        return String(plaintext)
    }

    /**
    * 从密码派生安全的256位(32字节)AES密钥
    * @param password 用户输入的密码
    * @param salt 盐值(应为每个用户随机生成并安全存储)
    * @return 32字节的AES密钥
    */
    private fun deriveKey(password: String, salt: ByteArray): ByteArray {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(
            password.toCharArray(),
            salt,
            ITERATIONS,
            KEY_LENGTH
        )
        return factory.generateSecret(spec).encoded
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun generateSaltString(): String {
        val salt = generateSalt();
        return salt.toHexString()
    }
    /**
    * 生成随机盐值
    */
    private fun generateSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(SALT_LENGTH)
        random.nextBytes(salt)
        return salt
    }

    /**
    * 将字节数组转换为Base64字符串便于存储
    */
    fun bytesToBase64(bytes: ByteArray): String {
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    /**
    * 将Base64字符串转换回字节数组
    */
    fun base64ToBytes(base64: String): ByteArray {
        return Base64.decode(base64, Base64.DEFAULT)
    }

}