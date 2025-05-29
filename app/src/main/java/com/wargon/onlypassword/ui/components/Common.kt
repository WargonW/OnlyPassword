package com.wargon.onlypassword.ui.components

import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.ChangeCircle
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wargon.onlypassword.R
import com.wargon.onlypassword.data.EncryptPasswordData
import com.wargon.onlypassword.ui.AppNavigationDest
import com.wargon.onlypassword.ui.AppNavigationItem
import com.wargon.onlypassword.ui.AppViewModel
import com.wargon.onlypassword.ui.AuthViewModel
import com.wargon.onlypassword.ui.OnlyPasswordViewModelProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CopyIconButton(
    text: String,
    onCopyOK: () -> Unit = {}
) {
    val clipBoard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    IconButton(
        onClick = {
            scope.launch {
                val clipData = ClipData.newPlainText("plain text",text)
                val clipEntry = ClipEntry(clipData)
                clipBoard.setClipEntry(clipEntry)
                onCopyOK()
            }
        }
    ) {
        Icon(
            imageVector = Icons.Default.ContentCopy,
            contentDescription = stringResource(R.string.Copy),
            tint = MaterialTheme.colorScheme.outline
        )
    }
}


/**
 * Top Bar根据名称定义
 */
enum class PwdAppTopBar{
    COMMON_BAR,
    SEARCH_BAR,
    NO_BAR,
}


@Composable
fun AppNavigationBottomBar(
    navItemList: List<AppNavigationItem>,
    currentNavigationDest: AppNavigationDest,
    onTabClick: (AppNavigationDest) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        for (navItem in navItemList) {
            NavigationBarItem(
                selected = currentNavigationDest == navItem.appNavDest,
                onClick = {onTabClick(navItem.appNavDest)},
                label = { Text(navItem.label) },
                icon = {
                    Icon(
                        imageVector = navItem.icon,
                        contentDescription = navItem.label
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigationTopBar (
    title: String,
    canNavigationBack: Boolean,
    navigateUp: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier
            .fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(title)
            },
            scrollBehavior = scrollBehavior,
            navigationIcon = {
                if (canNavigationBack) {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.Back)
                        )
                    }
                }
            },
        )
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    pwdCount: Int,
    onKeywordChanged: (String?) -> Unit,
    onMenuExpand: (Boolean) -> Unit,
    onSettingsClick: () -> Unit,
    onInfoClick: () -> Unit,
    appViewModel: AppViewModel = viewModel(factory = OnlyPasswordViewModelProvider.Factory),
    authViewModel: AuthViewModel = viewModel(factory = OnlyPasswordViewModelProvider.Factory),
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    //加载
    var dataLoading by remember {mutableStateOf(false)}
    var importLoading by remember { mutableStateOf(false) }

    //menu展开
    var menuExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    var encodePwdStr by remember {mutableStateOf("")}
    //导出
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = result.data?.data
            if (encodePwdStr.isNotEmpty()) {
                uri?.let {
                    try {
                        val writer = OutputStreamWriter(context.contentResolver.openOutputStream(it))
                        writer.use { w ->
                            w.write(encodePwdStr)
                        }
                        appViewModel.updateSnackBar(true, context.getString(R.string.Export_Success))
                        encodePwdStr = ""
                    } catch (e: Exception) {
                        e.printStackTrace()
                        appViewModel.updateSnackBar(true, context.getString(R.string.Export_Fail))
                    }
                }
            }else {
                appViewModel.updateSnackBar(true,
                    context.getString(R.string.No_Data_Could_No_Export))
            }
        }
    }
    //导入
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BufferedReader(inputStream.reader()).use {reader ->
                        val text = reader.readLines().joinToString("")
                        encodePwdStr = text

                        coroutineScope.launch(Dispatchers.Default) {
                            val json = Json {prettyPrint = true}
                            try {
                                val encryptPwdData = json.decodeFromString<EncryptPasswordData>(encodePwdStr)
                                val importSuccess = appViewModel.importEncryptPwdInfo(encryptPwdData)
                                if(importSuccess) {
                                    appViewModel.updateSnackBar(true,context.getString(R.string.Import_Success))
                                }else {
                                    appViewModel.updateSnackBar(true,context.getString(R.string.Parse_Failed_Pls_Confirm_Master_Pwd))
                                }
                            }catch (e: Exception){
                                appViewModel.updateSnackBar(true,
                                    context.getString(R.string.Parse_Failed_Pls_Confirm_Master_Pwd))
                            }

                            withContext(Dispatchers.Main) {
                                importLoading = false
                                menuExpanded = false;
                                onMenuExpand(false)
                            }
                        }
                    }
                }
            }catch (e: Exception) {
                appViewModel.updateSnackBar(true,context.getString(R.string.File_Import_Failed))
                importLoading = false
                menuExpanded = false;
                onMenuExpand(false)
            }
        }
        if(uri == null) {
            importLoading = false
            menuExpanded = false;
            onMenuExpand(false)
        }
    }


    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            CustomSearchBar(
                pwdCount = pwdCount,
                onKeywordChanged = onKeywordChanged
            )
        }

        Box() {
            IconButton(
                onClick = {
                    menuExpanded = !menuExpanded
                    onMenuExpand(menuExpanded)
                },
                modifier = Modifier.size(48.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false; onMenuExpand(false) }
            ) {
                // First section
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.Export_Password_File)) },
                    leadingIcon = {
                        if (dataLoading) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                AnimatedDownloadIcon()
                            }
                        } else {
                            Icon(Icons.Outlined.Download, contentDescription = null)
                        }
                    },
                    enabled = !dataLoading && !importLoading,
                    onClick = {
                        dataLoading = true
                        coroutineScope.launch(Dispatchers.Default) {
                            val encryptPwdData = appViewModel.generateEncryptPwdInfo();
                            val json = Json {prettyPrint = true}
                            val encodeStr = json.encodeToString(encryptPwdData)

                            withContext(Dispatchers.Main) {
                                dataLoading = false
                                if(encodeStr.isEmpty()) {
                                    appViewModel.updateSnackBar(true,context.getString(R.string.No_Data_Could_No_Export))
                                    return@withContext
                                }
                                encodePwdStr = encodeStr
                                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) // 定义格式
                                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                                    addCategory(Intent.CATEGORY_OPENABLE)
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TITLE, "onlyPwd-" + formatter.format(Date()))
                                }
                                createDocumentLauncher.launch(intent)
                                menuExpanded = false;
                                onMenuExpand(false)
                            }
                        }

                    }
                )

                DropdownMenuItem(
                    text = { Text(stringResource(R.string.Import_Password_File)) },
                    leadingIcon = {
                        if (importLoading) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                AnimatedDownloadIcon()
                            }
                        } else {
                            Icon(Icons.Outlined.Upload, contentDescription = null)
                        }
                    },
                    enabled = !dataLoading && !importLoading,
                    onClick = {
                        importLoading = true
                        //为了防止丢失Activity导致导入失败做特殊处理
                        authViewModel.importStart()
                        coroutineScope.launch(Dispatchers.Default) {
                            importLauncher.launch(arrayOf("text/plain"))
                        }
                    }
                )
                HorizontalDivider()

                DropdownMenuItem(
                    text = { Text(stringResource(R.string.Settings)) },
                    leadingIcon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                    onClick = {
                        menuExpanded = false;
                        onMenuExpand(false)
                        onSettingsClick()
                    }
                )

                HorizontalDivider()

                DropdownMenuItem(
                    text = { Text(stringResource(R.string.About)) },
                    leadingIcon = { Icon(Icons.Outlined.Info, contentDescription = null) },
                    onClick = {
                        menuExpanded = false;
                        onMenuExpand(false)
                        onInfoClick()
                    }
                )
            }
        }
    }
}



@Composable
fun CustomSearchBar(
    pwdCount: Int,
    onKeywordChanged: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }

    DisposableEffect(Unit) {
        onDispose {
            onKeywordChanged(null)
        }
    }

    Box(modifier = modifier) {
        BasicTextField(
            value = text,
            onValueChange = {
                text = it
                onKeywordChanged(text)
            },
            singleLine = true,
            decorationBox = { innerTextField ->
                Box(
                    contentAlignment = Alignment.CenterStart, // 确保内容从中心开始
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxSize()
                ) {
                    if (text.isEmpty()) {
                        Text(
                            text = stringResource(R.string.Search_Placeholder, pwdCount),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        )
                    }
                    innerTextField()
                }
            },
            textStyle = TextStyle.Default.copy(
                textAlign = TextAlign.Start,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.None
                )
            ),
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(16.dp))
        )
    }

}


@Composable
fun AnimatedDownloadIcon() {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 1000, easing = LinearEasing)
        )
    )

    Icon(
        imageVector = Icons.Outlined.ChangeCircle,
        contentDescription = null,
        modifier = Modifier
            .rotate(rotation)
    )
}