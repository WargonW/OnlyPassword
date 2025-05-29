package com.wargon.onlypassword

import android.app.Application
import android.content.Context
import com.wargon.onlypassword.data.AppContainer
import com.wargon.onlypassword.data.AppDataContainer
import com.wargon.onlypassword.encrypt.TinkEncryptionHelper

class OnlyPasswordApplication : Application(){

    lateinit var appContainer: AppContainer

    companion object {
        private lateinit var instance: OnlyPasswordApplication

        fun getAppContext(): Context {
            return instance.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        appContainer = AppDataContainer(this)
        TinkEncryptionHelper.init(this)
        instance = this

    }
}