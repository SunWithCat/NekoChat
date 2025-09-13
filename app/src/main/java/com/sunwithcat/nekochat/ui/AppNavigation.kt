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
    const val NEW_CHAT_SCREEN = "new-chat"
    const val CHAT_SCREEN = "chat/{conversationId}"
    const val SETTINGS_SCREEN = "SettingsScreen"
    const val ABOUT_SCREEN = "AboutScreen"
    const val HISTORY_SCREEN = "HistoryScreen"
}

fun navigateToNewChat(navController: NavController) {
    navController.navigate(Routes.NEW_CHAT_SCREEN) {
        popUpTo(navController.graph.startDestinationId) { inclusive = true }
        launchSingleTop = true
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.NEW_CHAT_SCREEN) {
        composable(Routes.NEW_CHAT_SCREEN) {
            ChatScreen(
                conversationId = -1L,
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS_SCREEN) },
                onNavigateToAbout = { navController.navigate(Routes.ABOUT_SCREEN) },
                onNavigateToHistory = { navController.navigate(Routes.HISTORY_SCREEN) },
                onNavigateToNewChat = { navigateToNewChat(navController) }
            )
        }
        composable(
            route = Routes.CHAT_SCREEN,
            arguments = listOf(navArgument("conversationId") { type = NavType.LongType })
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getLong("conversationId") ?: -1L
            ChatScreen(
                conversationId = conversationId,
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS_SCREEN) },
                onNavigateToAbout = { navController.navigate(Routes.ABOUT_SCREEN) },
                onNavigateToHistory = { navController.navigate(Routes.HISTORY_SCREEN) },
                onNavigateToNewChat = { navigateToNewChat(navController) }
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
                    navController.navigate("chat/$conversationId")
                }
            )
        }
    }
}
