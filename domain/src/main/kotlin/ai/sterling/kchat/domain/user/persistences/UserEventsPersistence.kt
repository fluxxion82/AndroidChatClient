package ai.sterling.kchat.domain.user.persistences

import ai.sterling.kchat.domain.user.models.UserEvent
import kotlinx.coroutines.channels.ReceiveChannel

interface UserEventsPersistence {

    fun events(): ReceiveChannel<UserEvent>

    suspend fun update(event: UserEvent)
}
