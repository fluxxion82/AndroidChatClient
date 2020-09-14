package ai.sterling.kchat.domain.user.persistences

import ai.sterling.kchat.domain.base.CoroutinesContextFacade
import ai.sterling.kchat.domain.user.models.UserEvent
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class InMemoryUserEventsPersistence @Inject constructor(
    private val contextFacade: CoroutinesContextFacade
) : UserEventsPersistence {

    private val channel = ConflatedBroadcastChannel<UserEvent>()

    override fun events(): ReceiveChannel<UserEvent> = channel.openSubscription()

    override suspend fun update(event: UserEvent) = withContext(contextFacade.default) {
        channel.send(event)
    }
}
