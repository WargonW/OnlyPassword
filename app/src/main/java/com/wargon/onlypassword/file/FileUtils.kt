package com.wargon.onlypassword.file

import android.content.Context
import android.os.Build
import android.os.Environment
import java.io.File
import java.io.FileOutputStream

object FileUtils {

    /**
    * 获取或创建文件
    */
    fun getOrCreateFile(
        context: Context,
        fileName: String,
        directoryType: String
    ): File {
        val directory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Environment.getExternalStoragePublicDirectory(directoryType)
        } else {
            context.getExternalFilesDir(directoryType)
        } ?: throw Exception("无法获取存储目录")

        if (!directory.exists()) {
            directory.mkdirs()
        }

        return File(directory, fileName).apply {
            if (!exists()) {
                createNewFile()
            }
        }
    }

    /**
    * 读取文件内容
    */
    fun readFile(context: Context, fileName: String, directoryType: String): String? {
        return try {
            val file = getOrCreateFile(context, fileName, directoryType)
            file.readText()
        } catch (e: Exception) {
            null
        }
    }
}