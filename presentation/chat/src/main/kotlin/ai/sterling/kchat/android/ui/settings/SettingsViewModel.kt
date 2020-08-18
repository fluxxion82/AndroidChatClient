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
import ai.sterling.logger.KLogger
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val getServerInfo: GetServerInfo,
    private val updateServerInfo: UpdateServerInfo,
    private val loginUser: LoginUser
) : BaseViewModel() {

    val username = MutableLiveData<String?>()
    val serverAddress = MutableLiveData<String?>()
    val serverPort = MutableLiveData<String?>()

    val navigation = MutableLiveData<Event<ServerInfoNavigation>?>()

    init {
        launch {
            val serverInfo = getServerInfo().single()

            username.value = serverInfo.username
            serverAddress.value = serverInfo.serverIP
            serverPort.value = serverInfo.serverPort.toString()

//            if (!username.value.isNullOrEmpty()) {
//                navigation.value = ServerInfoNavigation.UPDATE_SUCCEEDED.toEvent()
//            }
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
                    navigation.value = ServerInfoNavigation.UPDATE_SUCCEEDED.toEvent()
                }
            }
        } else {
            KLogger.d {
                "username is null or empty"
            }
            navigation.value = ServerInfoNavigation.UPDATE_ERROR.toEvent()
        }
    }
}