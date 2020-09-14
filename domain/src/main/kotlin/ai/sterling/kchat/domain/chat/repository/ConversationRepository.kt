package ai.sterling.kchat.domain.chat.repository

import ai.sterling.kchat.domain.chat.model.Conversation
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {
    fun getConversation(): Flow<Conversation>
    suspend fun updateChatPayloads(payload: List<String>)
}