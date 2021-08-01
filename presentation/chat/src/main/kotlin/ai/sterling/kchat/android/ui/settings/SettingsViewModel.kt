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
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getServerInfo: GetServerInfo,
    private val updateServerInfo: UpdateServerInfo,
    private val loginUser: LoginUser
) : BaseViewModel() {
    val username = MutableStateFlow<String>("")
    val serverAddress = MutableStateFlow<String>("10.0.0.62")
    val serverPort = MutableStateFlow<String>("")

    val navigation = MutableLiveData<Event<ServerInfoNavigation>?>()
    val error: MutableStateFlow<Outcome.Error?> = MutableStateFlow(null)

    init {
        launch {
            val serverInfo = getServerInfo().single()

            username.value = serverInfo.username
            serverAddress.value = serverInfo.serverIP
            serverPort.value = serverInfo.serverPort.toString()
        }
    }

    fun finishClicked() {
        if (!username.value.isNullOrEmpty()) {
            KLogger.d {
                "username is ${username.value}"
            }
            val serverInfo = ServerInfo(username.value!!, serverAddress.value!!, serverPort.value!!.toInt())
            launch {
                updateServerInfo(serverInfo)
            }
            launch {
                val loginState = loginUser(LoginUser.LoginData(username.value!!))
                if (loginState == UserState.LoggedIn) {
                    navigation.value = ServerInfoNavigation.UpdateSucceeded.toEvent()
                }
            }
        } else {
            KLogger.d {
                "username is null or empty"
            }
            navigation.value = ServerInfoNavigation.UpdateError.toEvent()
        }
    }
}