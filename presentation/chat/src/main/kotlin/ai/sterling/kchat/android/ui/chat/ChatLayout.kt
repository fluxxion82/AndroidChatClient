package ai.sterling.kchat.android.ui.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.composable
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import ai.sterling.kchat.android.ui.base.Event
import ai.sterling.kchat.android.ui.base.toEvent
import ai.sterling.kchat.android.ui.settings.models.ServerInfoNavigation
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.compose.ui.tooling.preview.Preview

@ExperimentalFoundationApi
@Composable
fun ChatContent(navController: NavController, viewModel: ChatViewModel) {

}