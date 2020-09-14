package ai.sterling.kchat.domain.chat.persistence

import ai.sterling.kchat.domain.chat.model.ChatMessage

interface ChatStorage {
    suspend fun insertChatMessages(messageList: List<ChatMessage>)
    suspend fun getChatMessage(id: Int): ChatMessage?
    suspend fun getAllChatMessages(): List<ChatMessage>
    suspend fun deleteAllMessages()
}