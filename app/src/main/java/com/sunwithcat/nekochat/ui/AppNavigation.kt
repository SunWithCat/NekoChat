package com.sunwithcat.nekochat.ui

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sunwithcat.nekochat.data.local.ApiKeyManager
import com.sunwithcat.nekochat.ui.about.AboutScreen
import com.sunwithcat.nekochat.ui.chat.ApiKeyInputDialog
import com.sunwithcat.nekochat.ui.chat.ChatScreen
import com.sunwithcat.nekochat.ui.history.HistoryScreen
import com.sunwithcat.nekochat.ui.settings.SettingsScreen
import kotlinx.coroutines.launch

object Routes {
    const val CHAT_SCREEN = "chat"
    const val SETTINGS_SCREEN = "SettingsScreen"
    const val ABOUT_SCREEN = "AboutScreen"
    const val HISTORY_SCREEN = "HistoryScreen"
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    // 监听当前导航目的地
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val context = LocalContext.current

    var showApikeyDialog by remember { mutableStateOf(false) }

    val safePopBackStack: () -> Unit = {
        val currentDestination = navController.currentDestination?.route

        // 检查是否还有可以返回的目的地
        when {
            currentDestination == null -> {
                android.util.Log.w(
                        "AppNavigation",
                        "Cannot pop back stack: current destination is null"
                )
            }
            currentDestination.startsWith("chat") -> {
                // 检查是否在主屏幕（ChatScreen），如果是则不允许返回
                android.util.Log.w(
                        "AppNavigation",
                        "Cannot pop back stack: already at ChatScreen (root)"
                )
            }
            else -> {
                try {
                    navController.popBackStack()
                    android.util.Log.d("AppNavigation", "popBackStack completed successfully")
                } catch (e: Exception) {
                    android.util.Log.e("AppNavigation", "Error popping back stack", e)
                }
            }
        }
    }

    if (showApikeyDialog) {
        ApiKeyInputDialog(
                onDismiss = {
                    showApikeyDialog = false
                    AppSessionManager.hasShownApiKeyPromptThisSession = true
                },
                onConfirm = { apiKey ->
                    val apiKeyManager = ApiKeyManager(context)
                    apiKeyManager.saveApiKey(apiKey)
                    Toast.makeText(context, "人家会好好记住这个秘密的喵~", Toast.LENGTH_SHORT).show()
                    AppSessionManager.hasShownApiKeyPromptThisSession = true
                    showApikeyDialog = false
                }
        )
    }

    ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = currentRoute?.startsWith(Routes.CHAT_SCREEN) == true,
            drawerContent = {
                ModalDrawerSheet(
                        drawerContainerColor =
                                MaterialTheme.colorScheme.primaryContainer.copy(0.98f)
                ) {
                    Column(modifier = Modifier.statusBarsPadding()) {
                        Column(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                    text = "Neko Chat",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                            )
                            Text(text = "你的专属猫娘助手", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Modifier.padding(horizontal = 16.dp)
                    HorizontalDivider(
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    NavigationDrawerItem(
                            icon = { Icon(Icons.Default.VpnKey, contentDescription = "设置API Key") },
                            label = { Text(text = "设置API Key") },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                showApikeyDialog = true
                            },
                            modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    NavigationDrawerItem(
                            icon = {
                                Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = "新对话")
                            },
                            label = { Text(text = "开始新对话") },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                navController.navigate(Routes.CHAT_SCREEN)
                            },
                            modifier = Modifier.padding(horizontal = 12.dp),
                            colors =
                                    NavigationDrawerItemDefaults.colors(
                                            unselectedContainerColor = Color.Transparent,
                                            selectedContainerColor =
                                                    MaterialTheme.colorScheme.primary.copy(
                                                            alpha = 0.2f
                                                    )
                                    )
                    )
                    NavigationDrawerItem(
                            icon = { Icon(Icons.Outlined.History, contentDescription = "历史记录") },
                            label = { Text(text = "历史记录") },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                navController.navigate(Routes.HISTORY_SCREEN)
                            },
                            modifier = Modifier.padding(horizontal = 12.dp),
                            colors =
                                    NavigationDrawerItemDefaults.colors(
                                            unselectedContainerColor = Color.Transparent,
                                            selectedContainerColor =
                                                    MaterialTheme.colorScheme.primary.copy(
                                                            alpha = 0.2f
                                                    )
                                    )
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    HorizontalDivider(
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    NavigationDrawerItem(
                            icon = { Icon(Icons.Outlined.Info, contentDescription = "关于") },
                            label = { Text(text = "关于 NekoChat") },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                navController.navigate(Routes.ABOUT_SCREEN)
                            },
                            modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
    ) {
        NavHost(navController = navController, startDestination = Routes.CHAT_SCREEN) {
            composable(
                    route = "${Routes.CHAT_SCREEN}?conversationId={conversationId}",
                    arguments =
                            listOf(
                                    navArgument("conversationId") {
                                        type = NavType.LongType
                                        defaultValue = -1L
                                    }
                            )
            ) { backStackEntry ->
                val conversationId = backStackEntry.arguments?.getLong("conversationId") ?: -1L
                ChatScreen(
                        conversationId = conversationId,
                        onNavigateToSettings = { id ->
                            navController.navigate("${Routes.SETTINGS_SCREEN}?conversationId=$id")
                        },
                        onOpenDrawer = {
                            // 使用更安全的方式打开 drawer
                            android.util.Log.d(
                                    "AppNavigation",
                                    "onOpenDrawer called, drawerState: isClosed=${drawerState.isClosed}, isAnimationRunning=${drawerState.isAnimationRunning}"
                            )
                            scope.launch {
                                try {
                                    drawerState.open()
                                } catch (e: Exception) {
                                    // 捕获并记录异常，避免崩溃
                                    android.util.Log.e("AppNavigation", "Error opening drawer", e)
                                }
                            }
                        },
                        onShowApiKeyDialog = { showApikeyDialog = true }
                )
            }
            composable(
                    route = "${Routes.SETTINGS_SCREEN}?conversationId={conversationId}",
                    arguments =
                            listOf(
                                    navArgument("conversationId") {
                                        type = NavType.LongType
                                        defaultValue = -1L
                                    }
                            )
            ) { backStackEntry ->
                val conversationId = backStackEntry.arguments?.getLong("conversationId") ?: -1L
                SettingsScreen(onBack = safePopBackStack, conversationId = conversationId)
            }
            composable(Routes.ABOUT_SCREEN) { AboutScreen(onBack = safePopBackStack) }
            composable(Routes.HISTORY_SCREEN) {
                HistoryScreen(
                        onBack = safePopBackStack,
                        onConversationClick = { conversationId ->
                            navigateToChat(navController, conversationId)
                        }
                )
            }
        }
    }
}

fun navigateToChat(navController: NavController, conversationId: Long = -1L) {
    val route = "${Routes.CHAT_SCREEN}?conversationId=$conversationId"
    navController.navigate(route) { popUpTo(navController.graph.id) { inclusive = true } }
}
