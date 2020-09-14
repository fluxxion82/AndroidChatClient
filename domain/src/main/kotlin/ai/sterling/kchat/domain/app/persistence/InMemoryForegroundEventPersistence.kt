package ai.sterling.kchat.domain.app.persistence

import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class InMemoryForegroundEventPersistence @Inject constructor() :
    ForegroundEventPersistence {

    private val foregroundEvent = ConflatedBroadcastChannel<Boolean>()

    override fun getForegroundEvent(): ReceiveChannel<Boolean> = foregroundEvent.openSubscription()

    override suspend fun update(foreground: Boolean) {
        foregroundEvent.send(foreground)
    }
}
