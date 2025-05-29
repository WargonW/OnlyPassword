package com.wargon.onlypassword.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wargon.onlypassword.ui.AppViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.wargon.onlypassword.R
import com.wargon.onlypassword.ui.MasterPwdNavDest

@Composable
fun Settings(
    appViewModel: AppViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(64.dp)
                .clickable(
                    onClick = {
                        navController.navigate(MasterPwdNavDest.route)
                    }
                )
        ){
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = Color.Red
            )
            Spacer(Modifier.padding(8.dp))

            Text(
                text = stringResource(R.string.Master_Password),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(16.dp)
                )
        }

    }
}


@Composable
fun SetMasterPassword(
    appViewModel: AppViewModel,
    setMasterPwdOk: () -> Unit,
    modifier: Modifier = Modifier
) {
    val minLength = 8
    val maxLength = 16
    var masterPwd by rememberSaveable { mutableStateOf("") }
    var confirmPwd by rememberSaveable { mutableStateOf("") }
    var masterPwdValidPass by rememberSaveable { mutableStateOf(false) }
    var confirmPwdValidPass by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(masterPwd,confirmPwd) {
        if(masterPwd.isNotBlank() && masterPwd.length in minLength..maxLength) {
            masterPwdValidPass = true
        }else {
            masterPwdValidPass = false
        }
        if(masterPwdValidPass && confirmPwd == masterPwd) {
            confirmPwdValidPass = true
        }else {
            confirmPwdValidPass = false
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(24.dp)
        ) {
            Text(
                text = stringResource(R.string.Set_Master_Password),
                style = MaterialTheme.typography.headlineLarge
            )
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
            Spacer(Modifier.padding(8.dp))
            OutlinedTextField(
                value = confirmPwd,
                onValueChange = {
                    if(it.length > maxLength) {
                        return@OutlinedTextField
                    }
                    confirmPwd = it
                },
                label = {
                    Text(stringResource(R.string.Input_Again_Master_Password))
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = confirmPwd.isNotEmpty() && !confirmPwdValidPass
            )

            Spacer(Modifier.padding(16.dp))
            Button(
                onClick = {
                    if(masterPwdValidPass && confirmPwdValidPass) {
                        appViewModel.setMasterPwd(masterPwd)
                        setMasterPwdOk()
                    }
                },
                enabled = masterPwdValidPass && confirmPwdValidPass,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.Save))
            }

            Spacer(Modifier.padding(8.dp))
            Box(modifier = Modifier.fillMaxWidth()){
                Text(
                    text = stringResource(R.string.Master_Pwd_Tip_Line1) + "\n" +
                            stringResource(R.string.Master_Password_Tip_Line2) + "\n" +
                            stringResource(R.string.Master_Password_Tip_Line3) + "\n" +
                            stringResource(R.string.Master_Password_Tip_Line4),
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}