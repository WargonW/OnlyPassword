package com.wargon.onlypassword.data

import android.content.Context

interface AppContainer {
    val passwordRepository: PasswordRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val passwordRepository: PasswordRepository by lazy {
        LocalPasswordRepository(PasswordDatabase.getDatabase(context).passwordDao())
    }

}