package ai.sterling.kchat.domain.user

import ai.sterling.kchat.domain.base.Usecase
import ai.sterling.kchat.domain.user.models.ProfileDetails
import ai.sterling.kchat.domain.user.models.UserEvent
import ai.sterling.kchat.domain.user.persistences.UserEventsPersistence
import ai.sterling.kchat.domain.user.persistences.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@Suppress("TooGenericExceptionCaught")
class GetProfileDetails @Inject constructor(
    private val repository: UserRepository,
    private val eventsPersistence: UserEventsPersistence
) : Usecase<Unit, Flow<ProfileDetails>> {

    override suspend fun invoke(param: Unit): Flow<ProfileDetails> =
        eventsPersistence.events().consumeAsFlow()
            .mapNotNull { (it as? UserEvent.DetailsChanged)?.details }
            .onStart {
                repository.getUserProfile().collect {
                    emit(it)
                }
            }
}
