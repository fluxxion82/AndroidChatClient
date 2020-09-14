package ai.sterling.kchat.domain.user.persistences

import ai.sterling.kchat.domain.settings.models.ServerInfo

interface UserPreferences {

    suspend fun getServerInfo(): ServerInfo

    suspend fun upsert(serverInfo: ServerInfo)

    suspend fun clear()
}
