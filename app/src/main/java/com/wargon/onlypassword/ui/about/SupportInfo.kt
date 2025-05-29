package com.wargon.onlypassword.ui.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wargon.onlypassword.R
import com.wargon.onlypassword.ui.theme.OnlyPasswordTheme

@Composable
fun About(
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)

    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.displayMedium
        )
        Text(
            text = stringResource(R.string.Only_Password_Slogan),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(Modifier.padding(24.dp))
        Text(
            text = stringResource(R.string.Only_Password_Note)
        )
        Spacer(Modifier.padding(8.dp))
        Text(
            text = stringResource(R.string.Developer) + ": 80900"
        )
        Text(
            text = stringResource(R.string.Contact_Me) + ": we890home@163.com"
        )
        HyperlinkText("https://www.890home.com/onlyPassword","OnlyPassword " + stringResource(R.string.Home_Website))
    }
}



@Composable
fun HyperlinkText(url: String, linkText: String) {
    val context = LocalContext.current

    // 创建带有样式的 AnnotatedString
    val annotatedString = buildAnnotatedString {
        pushStringAnnotation(tag = "URL", annotation = url)
        withStyle(style = SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline)) {
            append(linkText)
        }
        pop()
    }

    // 显示文本并处理点击事件
    Text(
        text = annotatedString,
        modifier = Modifier
            .padding(16.dp)
            .clickable {
                annotatedString.getStringAnnotations("URL", 0, annotatedString.length).firstOrNull()
                    ?.let { stringAnnotation ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(stringAnnotation.item))
                        // 启动浏览器
                        // 注意：你需要确保在真实的 Android 环境中运行此代码，因为模拟器可能无法正确处理外部意图。
                        // 可以通过 LocalContext.current 获取当前上下文来启动意图。
                        context.startActivity(intent)
                    }
            },
        color = Color.Black
    )
}


@Preview(showBackground = true)
@Composable
fun Preview() {
    OnlyPasswordTheme {
        About()
    }
}