package ai.sterling.kchat.domain.app.persistence

import kotlinx.coroutines.channels.ReceiveChannel

interface ForegroundEventPersistence {
    fun getForegroundEvent(): ReceiveChannel<Boolean>

    suspend fun update(foreground: Boolean)
}
