package com.sunwithcat.nekochat

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.sunwithcat.nekochat.data.local.ThemeManager
import com.sunwithcat.nekochat.ui.AppNavigation
import com.sunwithcat.nekochat.ui.theme.NekoChatTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeManager = remember { ThemeManager(this) }
            var themeMode by remember { mutableIntStateOf(themeManager.getThemeMode()) }

            val darkTheme = when (themeMode) {
                ThemeManager.MODE_LIGHT -> false
                ThemeManager.MODE_DARK -> true
                else -> isSystemInDarkTheme()
            }

            NekoChatTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        currentThemeMode = themeMode,
                        onThemeModeChange = { newMode ->
                            themeManager.saveThemeMode(newMode)
                            themeMode = newMode
                        }
                    )
                }
            }
        }
    }
}
