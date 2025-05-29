package com.wargon.onlypassword.ui.pwdAction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.wargon.onlypassword.R
import com.wargon.onlypassword.data.NAME_MAX_LENGTH
import com.wargon.onlypassword.data.NOTE_MAX_LENGTH
import com.wargon.onlypassword.data.PASSWORD_MAX_LENGTH
import com.wargon.onlypassword.data.Password
import com.wargon.onlypassword.data.USERNAME_MAX_LENGTH
import com.wargon.onlypassword.ui.AppViewModel
import com.wargon.onlypassword.ui.components.CopyIconButton
import kotlinx.coroutines.launch

@Composable
fun PwdEdit(
    id: Long? = null,
    appViewModel: AppViewModel,
    onSaveOk: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    // 表单状态
    var passwordInfo by remember{mutableStateOf(Password(0,"","","",null))}
    var validatePass by remember{mutableStateOf(false)}
    //密码
    var passwordVisible by remember { mutableStateOf(false) }
    //键盘控制
    val keyboardController = LocalSoftwareKeyboardController.current
    //提示框
    val openErrorAlertDialog = remember { mutableStateOf(false) }
    // 获取协程作用域
    val scope = rememberCoroutineScope()
    //校验失败提示
    val alertRes = remember { mutableIntStateOf(R.string.Pwd_Requirement) }
    //context
    val context = LocalContext.current

    LaunchedEffect(id) {
        if(id != null && id > 0) {
            val pwdInfo = appViewModel.getPwdInfo(id)
            if(pwdInfo != null) {
                passwordInfo = pwdInfo
            }
        }
    }


    // 卡片布局
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
                .imePadding() // 自动避开键盘
        ) {
            // 名称字段
            OutlinedTextField(
                value = passwordInfo.name,
                onValueChange = {
                    passwordInfo = passwordInfo.copy(name = it)
                    validatePass = appViewModel.validatePwdInfo(passwordInfo)
                },
                label = { Text(stringResource(R.string.Name) + "*") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.DriveFileRenameOutline,
                        contentDescription = null
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                supportingText = {
                    if (passwordInfo.name.isNotEmpty() && passwordInfo.name.length > NAME_MAX_LENGTH) {
                        Text(stringResource(R.string.Name_Length_Limit, NAME_MAX_LENGTH))
                    }
                },
                isError = passwordInfo.name.isNotEmpty() && passwordInfo.name.length > NAME_MAX_LENGTH
            )

            // 用户名字段
            OutlinedTextField(
                value = passwordInfo.username,
                onValueChange = {
                    passwordInfo = passwordInfo.copy(username = it)
                    validatePass = appViewModel.validatePwdInfo(passwordInfo)
                },
                label = { Text(stringResource(R.string.Username) + "*") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AccountBox,
                        contentDescription = null
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                supportingText = {
                    if (passwordInfo.username.isNotEmpty() && passwordInfo.username.length > USERNAME_MAX_LENGTH) {
                        Text(stringResource(R.string.Username_Length_Limit, USERNAME_MAX_LENGTH))
                    }
                },
                isError = passwordInfo.username.isNotEmpty() && passwordInfo.username.length > USERNAME_MAX_LENGTH
            )

            // 密码字段
            OutlinedTextField(
                value = passwordInfo.password,
                onValueChange = {
                    passwordInfo = passwordInfo.copy(password = it)
                    validatePass = appViewModel.validatePwdInfo(passwordInfo)
                },
                label = { Text(stringResource(R.string.Password) + "*") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) {
                                Icons.Default.Visibility
                            } else {
                                Icons.Default.VisibilityOff
                            },
                            contentDescription = if (passwordVisible) stringResource(R.string.Password_Hide) else stringResource(
                                R.string.Password_Show
                            )
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                supportingText = {
                    if (passwordInfo.password.isNotEmpty() && passwordInfo.password.length > PASSWORD_MAX_LENGTH) {
                        Text(stringResource(R.string.Password_Length_Limit, PASSWORD_MAX_LENGTH))
                    }
                },
                isError = passwordInfo.password.isNotEmpty() && passwordInfo.password.length > PASSWORD_MAX_LENGTH
            )

            // 备注字段
            OutlinedTextField(
                value = passwordInfo.note ?: "",
                onValueChange = {
                    passwordInfo = passwordInfo.copy(note = it)
                    validatePass = appViewModel.validatePwdInfo(passwordInfo)
                },
                label = { Text(stringResource(R.string.Note)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Notes,
                        contentDescription = null
                    )
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        // 当用户点击键盘上的 Done 按钮时执行
                        keyboardController?.hide() // 隐藏键盘
                        // 其他完成操作...
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                supportingText = {
                    if (passwordInfo.note != null && passwordInfo.note!!.length > NOTE_MAX_LENGTH) {
                        Text(stringResource(R.string.Note_Length_Limit, NOTE_MAX_LENGTH))
                    }
                },
                isError = passwordInfo.note != null && passwordInfo.note!!.length > NOTE_MAX_LENGTH
            )


            // 提交按钮
            Button(
                onClick = {
                    if (!appViewModel.validatePwdInfo(passwordInfo)) {
                        alertRes.value = R.string.Pwd_Requirement
                        openErrorAlertDialog.value = true
                    }
                    //edit
                    if (id != null && id > 0) {
                        scope.launch {
                            val duplicateName = appViewModel.checkDuplicateName(passwordInfo.name);
                            if(duplicateName) {
                                alertRes.value = R.string.Duplicate_Name
                                openErrorAlertDialog.value = true
                                return@launch
                            }
                            val savedResult = appViewModel.updatePwdInfo(passwordInfo)
                            if(!savedResult) {
                                alertRes.value = R.string.Unknown_Error
                                openErrorAlertDialog.value = true
                            }else {
                                onSaveOk(id)
                            }
                        }
                    }
                    //add
                    else {
                        scope.launch{
                            val duplicateName = appViewModel.checkDuplicateName(passwordInfo.name);
                            if(duplicateName) {
                                alertRes.value = R.string.Duplicate_Name
                                openErrorAlertDialog.value = true
                                return@launch
                            }
                            if(passwordInfo.id != 0L) {
                                appViewModel.updateSnackBar(true,
                                    context.getString(R.string.Add_Pwd_Could_Not_Set_Id))
                                return@launch
                            }
                            val savedResult = appViewModel.savePwdInfo(passwordInfo)
                            if(savedResult == null) {
                                alertRes.value = R.string.Unknown_Error
                                openErrorAlertDialog.value = true
                            }else {
                                onSaveOk(savedResult)
                            }
                        }
                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                enabled = validatePass,
                shape = MaterialTheme.shapes.large
            ) {
                Text(stringResource(R.string.Save), style = MaterialTheme.typography.labelLarge)
            }
        }
    }

    if(openErrorAlertDialog.value) {
        AlertDialog(
            onDismissRequest = { openErrorAlertDialog.value = false },
            title = {
                Text(text = stringResource(R.string.Pwd_Validate_failed))
            },
            text = {
                Text(text = stringResource(
                    alertRes.intValue,
                    NAME_MAX_LENGTH,
                    USERNAME_MAX_LENGTH,
                    PASSWORD_MAX_LENGTH,
                    NOTE_MAX_LENGTH
                ))
            },
            confirmButton = {
                TextButton(onClick = { openErrorAlertDialog.value = false }) { Text(stringResource(R.string.Confirm)) }
            }
        )
    }
}


@Composable
fun PwdView(
    id: Long,
    navigateUp: () -> Unit,
    onEdit: (Long) -> Unit,
    appViewModel: AppViewModel,
    modifier: Modifier = Modifier
) {

    var curPwdInfo by rememberSaveable { mutableStateOf<Password>(Password(0,"","","",null)) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var openConfirmDialog by rememberSaveable { mutableStateOf(false) }


    LaunchedEffect(id) {
        val pwdInfo = appViewModel.getPwdInfo(id)
        if(pwdInfo == null) {
            appViewModel.updateSnackBar(true, context.getString(R.string.Invalid_Data))
            navigateUp()
        }else {
            curPwdInfo = pwdInfo
        }
    }

    // 卡片布局
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column( // 自动避开键盘
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
                .imePadding()
        ) {
            Text(
                text = curPwdInfo.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )

            Column {
                Icon(
                    imageVector = Icons.Default.AccountBox,
                    contentDescription = stringResource(R.string.Username),
                    tint = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.padding(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = curPwdInfo.username,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    CopyIconButton(
                        curPwdInfo.username,
                        onCopyOK = {
                            appViewModel.updateSnackBar(true,
                                context.getString(R.string.Copy_Success))
                        }
                    )
                }

            }

            Column {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = stringResource(R.string.Password),
                    tint = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.padding(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = curPwdInfo.password,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    CopyIconButton(
                        curPwdInfo.password,
                        onCopyOK = {
                            appViewModel.updateSnackBar(true,
                                context.getString(R.string.Copy_Success))
                        }
                    )
                }
            }

            if(curPwdInfo.note != null && curPwdInfo.note!!.isNotEmpty()) {
                Column {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Note,
                        contentDescription = stringResource(R.string.Note),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.padding(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            text = curPwdInfo.note ?: "",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                        CopyIconButton(
                            curPwdInfo.note ?: "",
                            onCopyOK = {
                                appViewModel.updateSnackBar(true,
                                    context.getString(R.string.Copy_Success))
                            }
                        )
                    }
                }
            }


            Button(
                onClick = {
                    openConfirmDialog = true
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.Delete_Password))
            }

            Button(
                onClick = {
                    onEdit(id)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.Update_Password))
            }
        }
    }

    if(openConfirmDialog) {
        AlertDialog(
            onDismissRequest = { openConfirmDialog = false },
            title = {
                Text(text = stringResource(R.string.Delete_Pwd_Confirm))
            },
            text = {
                Text(text = stringResource(
                    R.string.Continue_or_not
                ))
            },
            confirmButton = {
                TextButton(onClick = {
                    openConfirmDialog = false
                    scope.launch {
                        val deleteResult = appViewModel.deletePwdInfo(curPwdInfo)
                        if(deleteResult) {
                            appViewModel.updateSnackBar(true,
                                context.getString(R.string.Delete_Success))
                            navigateUp()
                        }else {
                            appViewModel.updateSnackBar(true,
                                context.getString(R.string.Delete_Fail))
                        }
                    }
                }) { Text(stringResource(R.string.Confirm)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    openConfirmDialog = false
                }) { Text(stringResource(R.string.Cancel)) }
            }
        )
    }


}