package com.wargon.onlypassword.ui

import androidx.compose.ui.graphics.vector.ImageVector
import com.wargon.onlypassword.R
import com.wargon.onlypassword.ui.components.PwdAppTopBar

/**
 * 导航类型：底部，侧边、侧边抽屉
 */
enum class AppNavigation {
    NAVIGATION_BOTTOM,
    NAVIGATION_RAIL,
    NAVIGATION_DRAWER,
}

/**
 * 导航项目
 */
data class AppNavigationItem(
    val icon: ImageVector,
    val label: String,
    val appNavDest: AppNavigationDest,
)

enum class AppNavigationScreen {
    WAREHOUSE,
    GENERATOR,
    ADD,
    VIEW,
    EDIT,
    SETTINGS,
    MASTER_PWD,
    INIT_MASTER_PWD,
    ABOUT,
}

interface AppNavigationDest {
    val route: String
    val titleRes: Int
    val navigationBar: Boolean
    val pwdAppTopBar: PwdAppTopBar?
}

object PwdWarehouseNavDest : AppNavigationDest {
    override val route = AppNavigationScreen.WAREHOUSE.name
    override val titleRes = R.string.PWD_WAREHOUSE
    override val navigationBar = true
    override val pwdAppTopBar = PwdAppTopBar.SEARCH_BAR
}

object PwdGeneratorNavDest : AppNavigationDest {
    override val route = AppNavigationScreen.GENERATOR.name
    override val titleRes = R.string.PWD_GENERATOR
    override val navigationBar = true
    override val pwdAppTopBar = PwdAppTopBar.COMMON_BAR
}

object PwdAddNavDest : AppNavigationDest {
    override val route = AppNavigationScreen.ADD.name
    override val titleRes = R.string.PWD_ADD
    override val navigationBar = false
    override val pwdAppTopBar = PwdAppTopBar.COMMON_BAR
}

object PwdViewNavDest : AppNavigationDest {
    override val route = AppNavigationScreen.VIEW.name
    override val titleRes = R.string.PWD_VIEW
    override val navigationBar = false
    override val pwdAppTopBar = PwdAppTopBar.COMMON_BAR
}

object PwdEditNavDest : AppNavigationDest {
    override val route = AppNavigationScreen.EDIT.name
    override val titleRes = R.string.PWD_EDIT
    override val navigationBar = false
    override val pwdAppTopBar = PwdAppTopBar.COMMON_BAR
}

object SettingsNavDest : AppNavigationDest {
    override val route = AppNavigationScreen.SETTINGS.name
    override val titleRes = R.string.Settings
    override val navigationBar = false
    override val pwdAppTopBar = PwdAppTopBar.COMMON_BAR
}

object MasterPwdNavDest : AppNavigationDest {
    override val route = AppNavigationScreen.MASTER_PWD.name
    override val titleRes = R.string.Master_Pwd
    override val navigationBar = false
    override val pwdAppTopBar = PwdAppTopBar.COMMON_BAR
}


object InitMasterPwdNavDest : AppNavigationDest {
    override val route = AppNavigationScreen.INIT_MASTER_PWD.name
    override val titleRes = R.string.Master_Pwd
    override val navigationBar = false
    override val pwdAppTopBar = PwdAppTopBar.NO_BAR
}


object AboutNavDest : AppNavigationDest {
    override val route = AppNavigationScreen.ABOUT.name
    override val titleRes = R.string.About
    override val navigationBar = false
    override val pwdAppTopBar = PwdAppTopBar.COMMON_BAR
}

fun getAppNavDestByRoute(route: String): AppNavigationDest {
    return when {
        route.startsWith(AppNavigationScreen.WAREHOUSE.name) -> PwdWarehouseNavDest
        route.startsWith(AppNavigationScreen.GENERATOR.name) -> PwdGeneratorNavDest
        route.startsWith(AppNavigationScreen.ADD.name) -> PwdAddNavDest
        route.startsWith(AppNavigationScreen.VIEW.name) -> PwdViewNavDest
        route.startsWith(AppNavigationScreen.EDIT.name) -> PwdEditNavDest
        route.startsWith(AppNavigationScreen.SETTINGS.name) -> SettingsNavDest
        route.startsWith(AppNavigationScreen.MASTER_PWD.name) -> MasterPwdNavDest
        route.startsWith(AppNavigationScreen.INIT_MASTER_PWD.name) -> InitMasterPwdNavDest
        route.startsWith(AppNavigationScreen.ABOUT.name) -> AboutNavDest
        else -> PwdWarehouseNavDest
    }
}