package ai.sterling.kchat.android.ui.settings

import ai.sterling.kchat.android.ui.base.BaseViewModel
import ai.sterling.kchat.android.ui.base.Event
import ai.sterling.kchat.android.ui.base.toEvent
import ai.sterling.kchat.android.ui.settings.models.ServerInfoNavigation
import ai.sterling.kchat.domain.base.invoke
import ai.sterling.kchat.domain.initialization.models.UserState
import ai.sterling.kchat.domain.settings.GetServerInfo
import ai.sterling.kchat.domain.settings.UpdateServerInfo
import ai.sterling.kchat.domain.settings.models.ServerInfo
import ai.sterling.kchat.domain.user.LoginUser
import kotlinx.coroutines.flow.MutableStateFlow
import ai.sterling.kchat.domain.base.model.Outcome
import ai.sterling.logging.KLogger
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getServerInfo: GetServerInfo,
    private val updateServerInfo: UpdateServerInfo,
    private val loginUser: LoginUser
) : BaseViewModel() {
    val username = MutableStateFlow("")
    val serverAddress = MutableStateFlow("10.0.0.62")
    val serverPort = MutableStateFlow("4444")

//    private val channel = BroadcastChannel<Event<ServerInfoNavigation>>(1)
//    val navigation: Flow<Event<ServerInfoNavigation>?> = channel.openSubscription().consumeAsFlow()
    val navigation = MutableLiveData<Event<ServerInfoNavigation>?>()
    val error: MutableStateFlow<Outcome.Error?> = MutableStateFlow(null)

    init {
        launch {
            val serverInfo = getServerInfo().single()

            username.value = serverInfo.username.orEmpty()
            serverAddress.value = serverInfo.serverIP.orEmpty()
            serverPort.value = serverInfo.serverPort.toString()
        }
    }

    fun finishClicked(uName: String, address: String, port: String) {
        KLogger.d { "on finished clicked" }
        if (!uName.isEmpty()) {
            KLogger.d { "username is $uName" }
            val serverInfo = ServerInfo(uName, address, port.toInt())
            launch {
                val result = updateServerInfo(serverInfo)
                if (result) {
                    val loginState = loginUser(LoginUser.LoginData(uName))
                    if (loginState == UserState.LoggedIn) {
                        KLogger.w { "logged in" }
                        // channel.send(ServerInfoNavigation.UpdateSucceeded.toEvent())
                        navigation.value = ServerInfoNavigation.UpdateSucceeded.toEvent()
                    }
                }
            }
        } else {
            KLogger.d { "username is null or empty" }
            // launch { channel.send(ServerInfoNavigation.UpdateError.toEvent()) }
            navigation.value = ServerInfoNavigation.UpdateError.toEvent()
        }
    }
}
