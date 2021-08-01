package ai.sterling.kchat.android.ui.settings

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.asFlow
import androidx.navigation.compose.composable
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import ai.sterling.kchat.R
import ai.sterling.kchat.android.ui.base.Event
import ai.sterling.kchat.android.ui.base.toEvent
import ai.sterling.kchat.android.ui.settings.models.ServerInfoNavigation
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.hilt.navigation.compose.hiltViewModel

@ExperimentalFoundationApi
@Composable
fun SettingsContent(navController: NavController, viewModel: SettingsViewModel) {
    val nav by viewModel.navigation.asFlow().collectAsState(null)
    when (nav?.value) {
        ServerInfoNavigation.UpdateSucceeded -> {
            Toast.makeText(
                LocalContext.current,
                "update succeed",
                Toast.LENGTH_LONG
            ).show()
        }
        ServerInfoNavigation.UpdateError -> {
            Toast.makeText(
                LocalContext.current,
                "update error",
                Toast.LENGTH_LONG
            ).show()
        }
        ServerInfoNavigation.NoInternetError -> {
            Toast.makeText(
                LocalContext.current,
                "no internet",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    SettingsInputDialog(viewModel.username, viewModel.serverAddress, viewModel.serverPort, viewModel::finishClicked)
}

@Composable
fun SettingsInputDialog(
    userName: MutableStateFlow<String>,
    serverIp: MutableStateFlow<String>,
    serverPort: MutableStateFlow<String>,
    onClose:()->Unit
) {
    Column(
        modifier = Modifier.height(IntrinsicSize.Max).width(IntrinsicSize.Max)
    ) {
        Row(verticalAlignment = Alignment.Top, modifier = Modifier.height(IntrinsicSize.Min)) {
            Text(stringResource(R.string.enter_info))
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(IntrinsicSize.Min)) {
            TextField(
                value = userName.value,
                onValueChange = {
                    userName.value = it
                },
                label = { Text(stringResource(R.string.username_hint)) }
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(IntrinsicSize.Min)) {
            TextField(
                value = serverIp.value,
                onValueChange = {
                    serverIp.value = it
                },
                label = { Text(stringResource(R.string.server_address_hint)) }
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(IntrinsicSize.Min)) {
            TextField(
                value = serverPort.value,
                onValueChange = {
                    serverPort.value = it
                },
                label = { Text(stringResource(R.string.server_port_hint)) }
            )
        }

        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(20.dp)) {
            Button(onClick = { if (!userName.value.isNullOrEmpty()) {
                // navController.navigate("word_press_campaign/$text")
                onClose
            } }) {
                Text(text = "Go")
            }
        }
    }
}

@ExperimentalFoundationApi
@Preview(name = "Main content")
@Composable
fun PreviewMain() {
    val navController = rememberNavController()
    SettingsContent(navController, hiltViewModel())
}
