//import android.app.Activity
//import android.content.Context
//import androidx.biometric.BiometricManager
//import androidx.biometric.BiometricPrompt
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.platform.LocalContext
//import androidx.fragment.app.FragmentActivity
//import com.wargon.onlypassword.R
//import java.util.concurrent.Executors
//
//@Composable
//fun BiometricAuthScreen(
//    onAuthPass: () -> Unit,
//    onAuthFailed: () -> Unit,
//) {
//    val context = LocalContext.current
//    var showPrompt by remember { mutableStateOf(true) }
//
//    // 1. 初始化 BiometricPrompt
//    val biometricPrompt = remember {
//        BiometricPrompt(
//            context as FragmentActivity,
//            Executors.newSingleThreadExecutor(),
//            object : BiometricPrompt.AuthenticationCallback() {
//                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
//                    showPrompt = false
//                    (context as? Activity)?.finish()
//                }
//
//                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
//                    showPrompt = false
//                    onAuthPass()
//                }
//
//                override fun onAuthenticationFailed() {
//                    showPrompt = false
//                }
//            }
//        )
//    }
//
//    // 2. 配置 PromptInfo
//    val promptInfo = remember {
//        BiometricPrompt.PromptInfo.Builder()
//            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
//            .setTitle(context.getString(R.string.app_name) + context.getString(R.string.Already_Encrypt))
//            .setNegativeButtonText(context.getString(R.string.Use_Password))
//            .build()
//    }
//
//    // 3. 触发认证
//    LaunchedEffect(showPrompt) {
//        if (showPrompt) {
//            biometricPrompt.authenticate(promptInfo)
//        }
//    }
//}
//
//
//fun isBiometricAvailable(context: Context): Boolean {
//    val biometricManager = BiometricManager.from(context)
//    return biometricManager.canAuthenticate(
//        BiometricManager.Authenticators.BIOMETRIC_STRONG
//    ) == BiometricManager.BIOMETRIC_SUCCESS
//}