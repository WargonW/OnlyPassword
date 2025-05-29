package com.wargon.onlypassword.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.wargon.onlypassword.ui.about.About
import com.wargon.onlypassword.ui.components.AppNavigationBottomBar
import com.wargon.onlypassword.ui.components.AppNavigationTopBar
import com.wargon.onlypassword.ui.components.PwdAppTopBar
import com.wargon.onlypassword.ui.components.SearchTopBar
import com.wargon.onlypassword.ui.pwdAction.PwdEdit
import com.wargon.onlypassword.ui.pwdAction.PwdView
import com.wargon.onlypassword.ui.pwdGenerator.PwdGenerator
import com.wargon.onlypassword.ui.pwdwarehouse.PwdWarehouse
import com.wargon.onlypassword.ui.settings.SetMasterPassword
import com.wargon.onlypassword.ui.settings.Settings


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnlyPasswordApp(
    windowWidthSize: WindowWidthSizeClass,
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel = viewModel(factory = OnlyPasswordViewModelProvider.Factory),
    navController: NavHostController = rememberNavController()
) {

    //snackbar，用户全局的提示
    val snackBarHostState = remember { SnackbarHostState() }
    val snackBarState by appViewModel.snackBarState.collectAsState()

    //route
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route
    var currentNavDest: AppNavigationDest
    if(currentRoute != null) {
        currentNavDest = getAppNavDestByRoute(currentRoute)
    }else {
        currentNavDest = PwdWarehouseNavDest
    }

    //屏幕大小
    val navigationType: AppNavigation
    when (windowWidthSize) {
        WindowWidthSizeClass.Compact -> {
            navigationType = AppNavigation.NAVIGATION_BOTTOM
        }
        WindowWidthSizeClass.Medium -> {
            navigationType = AppNavigation.NAVIGATION_BOTTOM
        }
        WindowWidthSizeClass.Expanded -> {
            navigationType = AppNavigation.NAVIGATION_BOTTOM
        }
        else -> {
            navigationType = AppNavigation.NAVIGATION_BOTTOM
        }
    }
    val pwdListState = appViewModel.pwdListStateFlow.collectAsState()

    //导航 items
    val navigationItemList = listOf<AppNavigationItem>(
        AppNavigationItem(
            icon = Icons.AutoMirrored.Filled.List,
            label = stringResource(PwdWarehouseNavDest.titleRes),
            appNavDest = PwdWarehouseNavDest
        ),
        AppNavigationItem(
            icon = Icons.Default.Password,
            label = stringResource(PwdGeneratorNavDest.titleRes),
            appNavDest = PwdGeneratorNavDest
        )
    )

    var menuExpand by rememberSaveable { mutableStateOf(false) }


    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        //snack bar
        LaunchedEffect(snackBarState) {
            if(snackBarState.show){
                snackBarHostState.showSnackbar(
                    message = snackBarState.info,
                )
                appViewModel.updateSnackBar(false,"")
            }
        }

        Column(
            modifier = modifier.windowInsetsPadding(WindowInsets.systemBars)
//            modifier = modifier.padding(innerPadding)
        ) {
            //TopBar
            if (currentNavDest.pwdAppTopBar != null) {
                Surface(
                    modifier = Modifier
                        .height(64.dp)
                        .padding(vertical = 8.dp)
                ) {
                    when(currentNavDest.pwdAppTopBar) {
                        PwdAppTopBar.COMMON_BAR -> {
                            AppNavigationTopBar(
                                title = stringResource(currentNavDest.titleRes),
                                canNavigationBack = true,
                                navigateUp = {
                                    navController.navigateUp()
                                },
                                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
                            )
                        }
                        PwdAppTopBar.SEARCH_BAR -> {
                            SearchTopBar(
                                pwdCount = pwdListState.value.size,
                                onMenuExpand = {
                                    menuExpand = it
                                },
                                onKeywordChanged = {keyword -> appViewModel.updateSearchKeyword(keyword)},
                                onSettingsClick = {
                                    navController.navigate(SettingsNavDest.route)
                                },
                                onInfoClick = {
                                    navController.navigate(AboutNavDest.route)
                                },
                            )
                        }
                        else -> {
                            //no top bar
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                NavHost(
                    navController = navController,
                    startDestination = PwdWarehouseNavDest.route,
                    enterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(300)
                        ) + fadeIn(animationSpec = tween(300))
                    },
                    exitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = tween(300)
                        ) + fadeOut(animationSpec = tween(300))
                    },
                    popEnterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = tween(300)
                        ) + fadeIn(animationSpec = tween(300))
                    },
                    popExitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(300)
                        ) + fadeOut(animationSpec = tween(300))
                    }
                ) {
                    composable(
                        route = PwdWarehouseNavDest.route + "?targetId={targetId}",
                        enterTransition = {
                            slideInHorizontally(
                                initialOffsetX = { fullWidth -> -fullWidth },
                                animationSpec = tween(300)
                            ) + fadeIn(animationSpec = tween(300))
                        },
                        arguments = listOf(
                            navArgument("targetId") { type = NavType.LongType; defaultValue = -1L },
                        )
                    ) {backStackEntry ->
                        val targetId = backStackEntry.arguments?.getLong("targetId")
                        PwdWarehouse(
                            targetId = targetId,
                            appViewModel = appViewModel,
                            onAddBtnClick = {navController.navigate(PwdAddNavDest.route)},
                            onPwdItemClick = {id ->
                                navController.navigate(
                                    PwdViewNavDest.route + "?id=$id"
                                )
                            }
                        )
                    }
                    composable(route = PwdGeneratorNavDest.route) {
                        PwdGenerator(
                            appViewModel = appViewModel
                        )
                    }
                    composable(
                        route = PwdAddNavDest.route + "?id={id}",
                        arguments = listOf(
                            navArgument("id") { type = NavType.LongType; defaultValue = -1L }
                        )
                    ) {backStackEntry ->
                        val id = backStackEntry.arguments?.getLong("id")?:-1L
                        PwdEdit(
                            id = if (id > 0L) id else null,
                            onSaveOk = { targetId ->
                                navController.navigate(PwdWarehouseNavDest.route + "?targetId=$targetId"){
                                    popUpTo(PwdWarehouseNavDest.route + "?targetId={targetId}"){inclusive = true}
                                    // 避免重复创建实例
                                    launchSingleTop = true
                                }
                            },
                            appViewModel = appViewModel
                        )
                    }
                    composable(
                        route = PwdViewNavDest.route + "?id={id}",
                        arguments = listOf(
                            navArgument(name = "id") {type = NavType.LongType }
                        )
                    ) {backStackEntry ->
                        val id = backStackEntry.arguments?.getLong("id")?:-1L
                        PwdView(
                            id = id,
                            onEdit = {editId ->
                                navController.navigate(PwdEditNavDest.route + "?id=$editId")
                            },
                            appViewModel = appViewModel,
                            navigateUp = {navController.navigateUp()}

                        )
                    }
                    composable(
                        route = PwdEditNavDest.route + "?id={id}",
                        arguments = listOf(
                            navArgument("id") { type = NavType.LongType; defaultValue = -1L }
                        )
                    ) {backStackEntry ->
                        val id = backStackEntry.arguments?.getLong("id")?:-1L
                        PwdEdit(
                            id = if (id > 0L) id else null,
                            onSaveOk = { targetId ->
                                navController.navigate(PwdWarehouseNavDest.route + "?targetId=$targetId"){
                                    popUpTo(PwdWarehouseNavDest.route + "?targetId={targetId}"){inclusive = true}
                                    // 避免重复创建实例
                                    launchSingleTop = true
                                }
                            },
                            appViewModel = appViewModel
                        )
                    }

                    composable(
                        route = SettingsNavDest.route,
                    ) { backStackEntry ->
                        Settings(
                            appViewModel = appViewModel,
                            navController = navController
                        )
                    }

                    composable(
                        route = MasterPwdNavDest.route
                    ) { backStackEntry ->
                        SetMasterPassword(
                            setMasterPwdOk = {navController.navigateUp()},
                            appViewModel = appViewModel
                        )
                    }

                    composable(
                        route = AboutNavDest.route
                    ) {
                        About()
                    }
                }
            }


            AnimatedVisibility(visible =
                navigationType == AppNavigation.NAVIGATION_BOTTOM &&
                        currentNavDest.navigationBar
            ) {
                AppNavigationBottomBar(
                    navItemList = navigationItemList,
                    currentNavigationDest = currentNavDest,
                    onTabClick = {
                        var curDestRoute = navController.currentBackStackEntry?.destination?.route
                        if (curDestRoute != null && curDestRoute.contains("?")) {
                            curDestRoute = curDestRoute.substring(0,curDestRoute.indexOf("?"))
                        }
                        if (curDestRoute == it.route) {
                            return@AppNavigationBottomBar
                        }
                        navController.navigate(it.route){
                        if(it.route == PwdWarehouseNavDest.route) {
                            popUpTo(PwdWarehouseNavDest.route) {
                                inclusive = true
                            }
                            // 避免重复创建实例
                            launchSingleTop = true
                        }
                    } },
                )
            }
        }


        if (menuExpand) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)) // 半透明黑色遮罩
                    .clickable { menuExpand = false } // 点击遮罩关闭
            )
        }

    }

}

