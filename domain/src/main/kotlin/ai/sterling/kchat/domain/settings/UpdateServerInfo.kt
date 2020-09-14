package ai.sterling.kchat.domain.settings

import ai.sterling.kchat.domain.base.Usecase
import ai.sterling.kchat.domain.settings.models.ServerInfo
import ai.sterling.kchat.domain.user.persistences.UserPreferences
import javax.inject.Inject

class UpdateServerInfo @Inject constructor(
    private val preferences: UserPreferences
) : Usecase<ServerInfo, Unit> {

    override suspend fun invoke(param: ServerInfo) {
        preferences.upsert(param)
    }
}
