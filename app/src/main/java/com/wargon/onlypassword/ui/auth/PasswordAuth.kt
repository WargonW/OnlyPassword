package com.wargon.onlypassword.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wargon.onlypassword.R
import com.wargon.onlypassword.ui.AppViewModel
import com.wargon.onlypassword.ui.OnlyPasswordViewModelProvider
import com.wargon.onlypassword.ui.AuthViewModel
import kotlinx.coroutines.delay


@Composable
fun Locked(
    authViewModel: AuthViewModel = viewModel(factory = OnlyPasswordViewModelProvider.Factory),
    modifier: Modifier = Modifier
) {

    val lastLockTime by authViewModel.lastLockTime.collectAsState()
    var remainLockSeconds by remember {
        mutableStateOf((lastLockTime + authViewModel.loginFailedLockMills - System.currentTimeMillis()) / 1000)
    }

    var isLocking = lastLockTime + authViewModel.loginFailedLockMills > System.currentTimeMillis()
    LaunchedEffect(isLocking) {
        authViewModel.refreshLoginState()
        if (isLocking) {
            while (true) {
                delay(1000)
                remainLockSeconds = (lastLockTime + authViewModel.loginFailedLockMills - System.currentTimeMillis()) / 1000
                if(remainLockSeconds <= 0) {
                    authViewModel.clearLoginFailedInfo()
                    isLocking = false
                    break;
                }
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.App_Locked),
            color = Color.Red,
            style = MaterialTheme.typography.displayMedium
        )
        Spacer(Modifier.padding(8.dp))
        Text(
            text = stringResource(R.string.Lock_Desc),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.padding(24.dp))
        Text(
            text = stringResource(R.string.Remain_Lock_Time),
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(Modifier.padding(4.dp))
        if(remainLockSeconds > 60) {

            Text(
                text = "${remainLockSeconds/60} " + stringResource(R.string.Minute)
            )
        }else {
            Text(
                text = "$remainLockSeconds " + stringResource(R.string.Second)
            )
        }
    }
}

@Composable
fun PasswordAuth(
    authViewModel: AuthViewModel = viewModel(factory = OnlyPasswordViewModelProvider.Factory),
    appViewModel: AppViewModel = viewModel(factory = OnlyPasswordViewModelProvider.Factory),
    validatePass: () -> Unit,
    validateFailed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val minLength = 8
    val maxLength = 16
    var masterPwd by rememberSaveable { mutableStateOf("") }
    var masterPwdValidPass by rememberSaveable { mutableStateOf(false) }

    val loginFailedTimes by authViewModel.loginFailedTimes.collectAsState()

    val focusManager = LocalFocusManager.current

    LaunchedEffect(masterPwd) {
        if(masterPwd.isNotBlank() && masterPwd.length in minLength..maxLength) {
            masterPwdValidPass = true
        }else {
            masterPwdValidPass = false
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 120.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(24.dp)
        ) {
            Text(
                text = stringResource(R.string.Verify_Master_Pwd),
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(Modifier.padding(2.dp))
            Box(modifier = Modifier.fillMaxWidth()){
                Text(
                    text = stringResource(R.string.Verify_Master_Pwd_Note),
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Spacer(Modifier.padding(16.dp))
            OutlinedTextField(
                value = masterPwd,
                onValueChange = {
                    if(it.length > maxLength) {
                        return@OutlinedTextField
                    }
                    masterPwd = it
                },
                label = {
                    Text(stringResource(R.string._8_16_Master_Password))
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = masterPwd.isNotEmpty() && !masterPwdValidPass
            )

            Spacer(Modifier.padding(16.dp))
            Button(
                onClick = {
                    focusManager.clearFocus()
                    if(masterPwdValidPass) {
                        val validatePass = appViewModel.validateMasterPwd(masterPwd)
                        if(validatePass) {
                            validatePass()
                        }else {
                            validateFailed()
                        }
                    }
                },
                enabled = masterPwdValidPass,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.Confirm))
            }
            if(loginFailedTimes > 0) {
                Spacer(Modifier.padding(8.dp))
                Text(
                    text = stringResource(R.string.Auth_Failed_n_Times, loginFailedTimes),
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = stringResource(R.string.App_Lock_Warning),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}