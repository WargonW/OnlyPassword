package com.wargon.onlypassword.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wargon.onlypassword.OnlyPasswordApplication

object OnlyPasswordViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            AppViewModel(onlyPasswordApplication().appContainer.passwordRepository)
        }
        initializer {
            AuthViewModel()
        }
    }
}

/**
 * 定义一个CreationExtras的拓展函数，用来从其中获取Application
 * CreationExtras：Jetpack ViewModel 提供的工具类，用于在 ViewModel 初始化时传递依赖项。
 */
fun CreationExtras.onlyPasswordApplication(): OnlyPasswordApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as OnlyPasswordApplication)