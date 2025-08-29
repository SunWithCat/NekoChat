package com.sunwithcat.nekochat.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sunwithcat.nekochat.ui.about.AboutScreen
import com.sunwithcat.nekochat.ui.chat.ChatScreen
import com.sunwithcat.nekochat.ui.settings.SettingsScreen

object Routes {
    const val CHAT_SCREEN = "ChatScreen"
    const val SETTINGS_SCREEN = "SettingsScreen"
    const val ABOUT_SCREEN = "AboutScreen"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.CHAT_SCREEN
    ) {
        composable(Routes.CHAT_SCREEN) {
            ChatScreen(
                onNavigateToSettings = {
                    navController.navigate(Routes.SETTINGS_SCREEN)
                },
                onNavigateToAbout = {
                    navController.navigate(Routes.ABOUT_SCREEN)
                }
            )
        }
        composable(Routes.SETTINGS_SCREEN) {
            SettingsScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.ABOUT_SCREEN) {
            AboutScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

