package ai.sterling.kchat.android.platform.repositories

import ai.sterling.kchat.android.platform.internal.int
import ai.sterling.kchat.android.platform.internal.long
import ai.sterling.kchat.android.platform.internal.string
import ai.sterling.kchat.domain.base.CoroutinesContextFacade
import ai.sterling.kchat.domain.settings.models.ServerInfo
import ai.sterling.kchat.domain.user.models.AppUser
import ai.sterling.kchat.domain.user.persistences.UserPreferences
import android.content.Context
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import kotlinx.coroutines.withContext

internal class SharedUserPreferencesRepository @Inject constructor(
    context: Context,
    private val coroutineContextFacade: CoroutinesContextFacade
) : SharePreferencesRepository(context, PREFS_NAME), UserPreferences {

    private var username by sharedPreferences.string(USERNAME, "")
    private var serverIp by sharedPreferences.string(SERVER_IP, "10.0.0.63")
    private var serverPort by sharedPreferences.int(SERVER_PORT, 4444)

    override suspend fun getServerInfo(): ServerInfo = coroutineScope {
        withContext(coroutineContextFacade.io) {
            ServerInfo(
                username!!,
                serverIp!!,
                serverPort!!
            )
        }
    }

    override suspend fun upsert(serverInfo: ServerInfo) = coroutineScope {
        withContext(coroutineContextFacade.io) {
            username = serverInfo.username
            serverIp = serverInfo.serverIP
            serverPort = serverInfo.serverPort
        }
    }

    override suspend fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "server_info"
        private const val USERNAME = "username"
        private const val SERVER_IP = "server_ip"
        private const val SERVER_PORT = "server_port"
    }
}
