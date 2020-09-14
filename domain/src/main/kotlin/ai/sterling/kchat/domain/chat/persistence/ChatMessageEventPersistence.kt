package ai.sterling.kchat.domain.chat.persistence

import ai.sterling.kchat.domain.chat.model.MessageEvent
import kotlinx.coroutines.channels.ReceiveChannel

interface ChatMessageEventPersistence {
    fun events(): ReceiveChannel<MessageEvent>

    suspend fun update(event: MessageEvent)
}