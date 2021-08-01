package ai.sterling.kchat.android.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ai.sterling.kchat.android.ui.chat.ChatContent
import ai.sterling.kchat.android.ui.settings.SettingsContent

@ExperimentalFoundationApi
@Composable
fun KChat() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "settings"
    ) {
        composable("settings") {
            SettingsContent(navController = navController, hiltViewModel())
        }
        composable("chat") { backStackEntry ->
            ChatContent(navController = navController, hiltViewModel())
        }
    }
}
