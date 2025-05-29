package com.wargon.onlypassword.ui.pwdGenerator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.wargon.onlypassword.R
import com.wargon.onlypassword.ui.AppViewModel
import com.wargon.onlypassword.ui.components.CopyIconButton
import generatePwd


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PwdGenerator(
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel
) {
    val focusManager = LocalFocusManager.current

    Card(
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        val minLength = 3
        val maxLength = 50
        val initLength = 8
        var pwdLength by rememberSaveable { mutableIntStateOf(initLength) }
        var includeCharacter by rememberSaveable { mutableStateOf(true) }
        var includeNumber by rememberSaveable { mutableStateOf(true) }
        var includeSpecialChar by rememberSaveable { mutableStateOf(true) }
        var startsWithCharacter by rememberSaveable { mutableStateOf(false) }

        var generatedPwd by rememberSaveable { mutableStateOf("") }
        var validatePass by rememberSaveable { mutableStateOf(true) }
        val context = LocalContext.current

        LaunchedEffect(pwdLength,includeCharacter,includeNumber,includeSpecialChar,startsWithCharacter) {
            if(pwdLength !in minLength..maxLength){
                validatePass = false
                return@LaunchedEffect
            }
            if(!includeCharacter && !includeNumber && !includeSpecialChar) {
                validatePass = false
                return@LaunchedEffect
            }
            if(!includeCharacter && startsWithCharacter) {
                validatePass = false
                return@LaunchedEffect
            }
            validatePass = true
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(24.dp)// 点击任意位置清除焦点
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.Password_Length),
                    modifier = Modifier
                        .width(100.dp)
                )
                NumberInputField(
                    initValue = initLength,
                    onValueChange = {pwdLength = it},
                    min = minLength,
                    max = maxLength,
                    label = "$minLength ~ $maxLength",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.Including_Number),
                    modifier = Modifier
                        .width(100.dp)
                )
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    Switch(
                        checked = includeNumber,
                        onCheckedChange = {
                            focusManager.clearFocus() // 手动清除焦点
                            if(!includeCharacter && !includeSpecialChar) {
                                return@Switch
                            }
                            includeNumber = it
                        },
                        thumbContent = if (includeNumber) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize),
                                )
                            }
                        } else {
                            null
                        },
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.Including_Special_Character),
                    modifier = Modifier
                        .width(100.dp)
                )
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    Switch(
                        checked = includeSpecialChar,
                        onCheckedChange = {
                            focusManager.clearFocus() // 手动清除焦点
                            if(!includeCharacter && !includeNumber) {
                                return@Switch
                            }
                            includeSpecialChar = it
                        },
                        thumbContent = if (includeSpecialChar) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize),
                                )
                            }
                        } else {
                            null
                        },
                    )
                }
            }


            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.Including_Letter),
                    modifier = Modifier
                        .width(100.dp)
                )
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    Switch(
                        checked = includeCharacter,
                        onCheckedChange = {
                            focusManager.clearFocus() // 手动清除焦点
                            if(!includeSpecialChar && !includeNumber) {
                                return@Switch
                            }
                            if(startsWithCharacter && it == false) {
                                return@Switch
                            }
                            includeCharacter = it
                        },
                        thumbContent = if (includeCharacter) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize),
                                )
                            }
                        } else {
                            null
                        },
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.Starting_With_Letter),
                    modifier = Modifier
                        .width(100.dp)
                )
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    Switch(
                        checked = startsWithCharacter,
                        onCheckedChange = {
                            focusManager.clearFocus() // 手动清除焦点
                            startsWithCharacter = it
                            if(startsWithCharacter && !includeCharacter) {
                                includeCharacter = true
                            }
                        },
                        thumbContent = if (startsWithCharacter) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize),
                                )
                            }
                        } else {
                            null
                        },
                    )
                }
            }

            Button(
                onClick = {
                    focusManager.clearFocus() // 手动清除焦点
                    generatedPwd = generatePwd(
                        length = pwdLength,
                        includeNumber = includeNumber,
                        includeCharacter = includeCharacter,
                        includeSpecialChar = includeSpecialChar,
                        startsWithCharacter = startsWithCharacter
                    )
                },
                enabled = validatePass,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.Generate_Password)
                )
            }

            if(generatedPwd.isNotBlank()) {
                SelectionContainer {
                    Text(
                        text = generatedPwd,
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    CopyIconButton(
                        generatedPwd,
                        onCopyOK = {
                            appViewModel.updateSnackBar(true,
                                context.getString(R.string.Copy_Success))
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberInputField(
    initValue: Int,
    onValueChange: (Int) -> Unit,
    min: Int,
    max: Int,
    modifier: Modifier = Modifier,
    label: String? = null
) {
    //键盘控制
    val keyboardController = LocalSoftwareKeyboardController.current

    var textValue by remember(initValue) { mutableStateOf(initValue.toString()) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
    ) {
        // 减号按钮
        IconButton(
            onClick = {
                if (textValue.toInt() <= min) {
                    return@IconButton
                }
                textValue = (textValue.toInt() -1).toString()
                onValueChange(textValue.toInt())
            },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(Icons.Default.Remove, contentDescription = stringResource(R.string.Minus))
        }

        // 输入框
        OutlinedTextField(
            value = textValue,
            onValueChange = { newValue ->
                if (newValue.isEmpty()) {
                    textValue = ""
                    onValueChange(0)
                } else if (newValue.all { it.isDigit() }) {
                    if(newValue.toInt() in 1..max) {
                        textValue = newValue
                        onValueChange(newValue.toInt())
                    }
                }
            },
            label = label?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Number
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    // 当用户点击键盘上的 Done 按钮时执行
                    keyboardController?.hide() // 隐藏键盘
                    // 其他完成操作...
                }
            ),
            modifier = Modifier.weight(1f),
            isError = textValue.isEmpty() || !textValue.isDigitsOnly() || textValue.toInt() !in min..max
        )

        // 加号按钮
        IconButton(
            onClick = {
                if (textValue.toInt() >= max) {
                    return@IconButton
                }
                textValue = (textValue.toInt() + 1).toString()
                onValueChange(textValue.toInt())

            },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.Add))
        }
    }
}
