package ai.sterling.kchat.android.ui.settings

import ai.sterling.kchat.R
import ai.sterling.kchat.android.ui.settings.models.ServerInfoNavigation
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.asFlow
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@ExperimentalFoundationApi
@Composable
fun SettingsContent(navController: NavController, viewModel: SettingsViewModel) {
    val nav by viewModel.navigation.asFlow().collectAsState(null)

    // for some reason when I accessed nav.value it would re-trigger a recomposition
    nav?.consume {
        when (it) {
            ServerInfoNavigation.UpdateSucceeded -> {
                println("nav to chat screen")
                navController.navigate("chat")
            }
            ServerInfoNavigation.UpdateError -> {
                println("update error")
            }
            ServerInfoNavigation.NoInternetError -> {
                println("no internet")
            }
        }
    }

    SettingsInputDialog(viewModel.username.value, viewModel.serverAddress.value, viewModel.serverPort.value, viewModel::finishClicked)
}

@Composable
fun SettingsInputDialog(
    userName: String,
    serverIp: String,
    serverPort: String,
    onClose:(String, String, String) -> Unit
) {
    Column(
        modifier = Modifier
            .height(IntrinsicSize.Max)
            .width(IntrinsicSize.Max)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            Text(stringResource(R.string.enter_info))
        }

        var uName: String by remember { mutableStateOf(userName) }
        var sIp: String by remember { mutableStateOf(serverIp) }
        var sPort: String by remember { mutableStateOf(serverPort) }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            TextField(
                value = uName,
                onValueChange = {
                    uName = it
                },
                label = { Text(stringResource(R.string.username_hint)) }
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            TextField(
                value = sIp,
                onValueChange = {
                    sIp = it
                },
                label = { Text(stringResource(R.string.server_address_hint)) }
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            TextField(
                value = sPort,
                onValueChange = {
                    sPort = it
                },
                label = { Text(stringResource(R.string.server_port_hint)) }
            )
        }

        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(20.dp)) {
            Button(onClick = {
                onClose(uName, sIp, sPort)
            }) {
                Text(text = "Go")
            }
        }
    }
}

@ExperimentalFoundationApi
@Preview(name = "Settings content")
@Composable
fun PreviewSettings() {
    val navController = rememberNavController()
    SettingsContent(navController, hiltViewModel())
}
