package ai.sterling.kchat.domain.user

import ai.sterling.kchat.domain.base.Usecase
import ai.sterling.kchat.domain.initialization.models.UserState
import ai.sterling.kchat.domain.user.models.AppUser
import ai.sterling.kchat.domain.user.models.UserEvent
import ai.sterling.kchat.domain.user.persistences.UserEventsPersistence
import ai.sterling.kchat.domain.user.persistences.UserRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetUserState @Inject constructor(
    private val userRepository: UserRepository,
    private val userEvents: UserEventsPersistence
) : Usecase<Long, UserState> {

    override suspend fun invoke(param: Long): UserState {
        val user = userRepository.getUser(param).first()
        userEvents.update(UserEvent.LoginChanged(user))
        return when (user) {
            is AppUser.Anonymous -> UserState.Anonymous
            is AppUser.LoggedIn -> UserState.LoggedIn
        }
    }
}
