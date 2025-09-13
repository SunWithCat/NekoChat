package com.sunwithcat.nekochat.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sunwithcat.nekochat.ui.about.AboutScreen
import com.sunwithcat.nekochat.ui.chat.ChatScreen
import com.sunwithcat.nekochat.ui.history.HistoryScreen
import com.sunwithcat.nekochat.ui.settings.SettingsScreen

object Routes {
    const val CHAT_SCREEN = "chat"
    const val SETTINGS_SCREEN = "SettingsScreen"
    const val ABOUT_SCREEN = "AboutScreen"
    const val HISTORY_SCREEN = "HistoryScreen"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.CHAT_SCREEN) {
        composable(
            route = "${Routes.CHAT_SCREEN}?conversationId={conversationId}",
            arguments = listOf(
                navArgument("conversationId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getLong("conversationId") ?: -1L
            ChatScreen(
                conversationId = conversationId,
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS_SCREEN) },
                onNavigateToAbout = { navController.navigate(Routes.ABOUT_SCREEN) },
                onNavigateToHistory = { navController.navigate(Routes.HISTORY_SCREEN) },
                onNavigateToNewChat = {
                    navigateToChat(navController)
                }
            )
        }
        composable(Routes.SETTINGS_SCREEN) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.ABOUT_SCREEN) { AboutScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.HISTORY_SCREEN) {
            HistoryScreen(
                onBack = { navController.popBackStack() },
                onConversationClick = { conversationId ->
                    navigateToChat(navController, conversationId)
                }
            )
        }
    }
}

fun navigateToChat(navController: NavController, conversationId: Long = -1L) {
    val route = "${Routes.CHAT_SCREEN}?conversationId=$conversationId"
    navController.navigate(route) {
        popUpTo(navController.graph.id) {
            inclusive = true
        }
    }
}