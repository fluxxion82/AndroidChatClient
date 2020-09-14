package ai.sterling.kchat.domain.user

import ai.sterling.kchat.domain.base.CoroutineScopeFacade
import ai.sterling.kchat.domain.initialization.AppInitializer
import ai.sterling.kchat.domain.user.models.AppUser
import ai.sterling.kchat.domain.user.persistences.UserPreferences
import ai.sterling.kchat.domain.user.services.UserAwareService
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.jvm.JvmSuppressWildcards

internal class UserInfoInitializer @Inject constructor(
    private val getAppUser: GetAppUserByUsername,
    private val userPreferences: UserPreferences,
    private val services: Set<@JvmSuppressWildcards UserAwareService>,
    private val scopeFacade: CoroutineScopeFacade
) : AppInitializer {

    override suspend fun initialize() {
        scopeFacade.globalScope.launch {
            getAppUser(userPreferences.getServerInfo().username)
                .collect {
                    notifyAll(it)
                }
        }
    }

    private suspend fun notifyAll(user: AppUser) = coroutineScope {
        services.forEach { service ->
            launch {
                service.onUserChanged(user)
            }
        }
    }
}
