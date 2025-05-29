package com.wargon.onlypassword

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wargon.onlypassword.ui.OnlyPasswordViewModelProvider
import com.wargon.onlypassword.ui.OnlyPasswordApp
import com.wargon.onlypassword.ui.auth.PasswordAuth
import com.wargon.onlypassword.ui.components.AnimatedDownloadIcon
import com.wargon.onlypassword.ui.settings.SetMasterPassword
import com.wargon.onlypassword.ui.theme.OnlyPasswordTheme
import androidx.compose.ui.unit.dp
import com.wargon.onlypassword.ui.AppViewModel
import com.wargon.onlypassword.ui.AuthViewModel
import com.wargon.onlypassword.ui.auth.Locked

class MainActivity : ComponentActivity() {

    private lateinit var authViewModel: AuthViewModel

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        authViewModel = androidx.lifecycle.ViewModelProvider(owner = this, factory = OnlyPasswordViewModelProvider.Factory)
            .get(AuthViewModel::class.java)

        setContent {
            OnlyPasswordTheme {
                FirstScreen(
                    activity = this,
                    windowWidthSize = calculateWindowSizeClass(this).widthSizeClass
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        authViewModel.onStart()
    }
}

@Composable
fun FirstScreen(
    activity: Activity,
    windowWidthSize: WindowWidthSizeClass,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel(factory = OnlyPasswordViewModelProvider.Factory),
    appViewModel: AppViewModel = viewModel(factory = OnlyPasswordViewModelProvider.Factory)
    ) {
    val lastLockTime by authViewModel.lastLockTime.collectAsState()

    val isLocking by remember(lastLockTime){
        derivedStateOf { lastLockTime + authViewModel.loginFailedLockMills > System.currentTimeMillis() }
    }

    LaunchedEffect(Unit) {
        authViewModel.refreshLoginState()
    }


    if(isLocking) {
        Locked(
            modifier = Modifier.padding(top = 100.dp)
        )
    }
    else {
        val needAuth by authViewModel.needAuth.collectAsState()
        //主密码
        appViewModel.checkHasMasterPwd()
        val hasMasterPwd by appViewModel.hasMasterPwd.collectAsState()

        if(!hasMasterPwd) {
            SetMasterPassword(
                setMasterPwdOk = {
                    appViewModel.checkHasMasterPwd()
                },
                appViewModel = appViewModel,
                modifier = Modifier.padding(top = 64.dp)
            )
        }else {
            if(needAuth) {
                Scaffold(
                    modifier = modifier
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        PasswordAuth(
                            appViewModel = appViewModel,
                            validatePass = {
                                authViewModel.authOk()
                                authViewModel.clearLoginFailedInfo()
                            },
                            validateFailed = {
                                authViewModel.loginFailedOnce()
                            }
                        )
                    }
                }
            } else {
                OnlyPasswordApp(
                    windowWidthSize = windowWidthSize,
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    OnlyPasswordTheme {
        DropdownMenuItem(
            text = { Text("xxx") },
            leadingIcon = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    AnimatedDownloadIcon()
                }
            },
            onClick = {}
        )
    }
}
