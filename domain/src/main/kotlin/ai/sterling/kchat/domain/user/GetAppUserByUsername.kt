package ai.sterling.kchat.domain.user

import ai.sterling.kchat.domain.base.Usecase
import ai.sterling.kchat.domain.user.models.AppUser
import ai.sterling.kchat.domain.user.models.UserEvent
import ai.sterling.kchat.domain.user.persistences.UserRepository
import ai.sterling.kchat.domain.user.persistences.UserEventsPersistence
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GetAppUserByUsername @Inject constructor(
    private val userRepository: UserRepository,
    private val userEvents: UserEventsPersistence
) : Usecase<String, Flow<AppUser>> {

    override suspend fun invoke(param: String): Flow<AppUser> =
        userEvents.events().consumeAsFlow()
            .mapNotNull { (it as? UserEvent.LoginChanged)?.user }
            .onStart {
                userRepository.getUser(param)
            }
}