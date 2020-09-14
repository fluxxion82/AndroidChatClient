package ai.sterling.kchat.domain.chat.persistence

import ai.sterling.kchat.domain.chat.model.ChatMessage

interface ChatDao {
    fun insertChatMessage(message: ChatMessage): Boolean
    fun insertAllMessages(messageList: List<ChatMessage>): Boolean
    fun getAllMessages(): List<ChatMessage>
    fun getChatMessage(id: Int): ChatMessage?
    fun deleteAllMessages(): Boolean
}