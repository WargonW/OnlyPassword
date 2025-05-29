package com.wargon.onlypassword.ui.pwdwarehouse

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.wargon.onlypassword.R
import com.wargon.onlypassword.data.Password
import com.wargon.onlypassword.ui.AppViewModel
import com.wargon.onlypassword.ui.components.CopyIconButton


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PwdWarehouse(
    targetId: Long? = null,
    appViewModel: AppViewModel,
    onPwdItemClick: (Long) -> Unit,
    onAddBtnClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pwdList by appViewModel.pwdListStateFlow.collectAsState()
    val listState = rememberLazyListState()
    val keyword by appViewModel.searchKeyword.collectAsState()
    val context = LocalContext.current

    // 当 pwdList 或 targetName 变化时触发滚动
    LaunchedEffect(pwdList, targetId) {
        if (filterPassword(pwdList, keyword).isNotEmpty() && targetId != null) {
            val index = pwdList.indexOfFirst { it.id == targetId }
            if (index != -1) {
                listState.animateScrollToItem(index)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
        ) {
            val filterItems = filterPassword(pwdList, keyword)
            itemsIndexed(items = filterItems, key = {index, item -> item.id}) { index, item ->
                PwdInfoCard(
                    id = item.id,
                    name = item.name,
                    username = item.username,
                    password = item.password,
                    onCopyOk = {
                        appViewModel.updateSnackBar(true,
                            context.getString(R.string.Copy_Success))
                    },
                    onPwdItemClick = onPwdItemClick
                )
                val isLastItem = index == filterItems.lastIndex // 判断是否是最后一个
                if(isLastItem) {
                    Spacer(Modifier.padding(64.dp))
                }

            }
        }

        FloatingActionButton(
            onClick = onAddBtnClick,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.BottomEnd)

        ) {
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = "Add",
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }


}

private fun filterPassword(pwdList: List<Password>, keyword: String?): List<Password> {
    if (keyword == null) {
        return pwdList
    }
    return pwdList
        .filter { ele ->
            ele.name.lowercase().contains(keyword.lowercase()) ||
                    ele.username.lowercase().contains(keyword.lowercase())
        }
        .toList();
}

@Composable
fun PwdInfoCard(
    id: Long,
    name: String,
    username: String,
    password: String,
    onPwdItemClick: (Long) -> Unit,
    onCopyOk: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pwdVisible by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .clickable { onPwdItemClick(id) }
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        colors = CardDefaults.cardColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBox,
                    contentDescription = stringResource(R.string.Username),
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = username,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(16.dp))
                CopyIconButton(
                    username,
                    onCopyOK = onCopyOk
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = stringResource(R.string.Password),
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = if (pwdVisible) password else "****************",
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(16.dp))
                IconButton(
                    onClick = { pwdVisible = !pwdVisible },
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures{}
                    }
                ) {
                    Icon(
                        imageVector = if (pwdVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (pwdVisible) stringResource(R.string.Password_Show) else stringResource(R.string.Password_Hide),
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(24.dp),
                    )
                }
                Spacer(Modifier.width(16.dp))
                CopyIconButton(
                    password,
                    onCopyOK = onCopyOk
                )
            }
        }
    }
}
