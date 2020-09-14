package ai.sterling.kchat.domain.app

import ai.sterling.kchat.domain.base.CoroutineScopeFacade
import ai.sterling.kchat.domain.base.Usecase
import ai.sterling.kchat.domain.user.persistences.UserRepository
import javax.inject.Inject

class ExitApp @Inject constructor(
    private val userRepository: UserRepository,
    private val contextScope: CoroutineScopeFacade
) : Usecase<Unit, Unit> {

    override suspend fun invoke(param: Unit) = userRepository.disconnect()
}