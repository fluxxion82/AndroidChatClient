package ai.sterling.kchat.android.database.storage

import ai.sterling.kchat.android.database.DatabaseManager
import ai.sterling.kchat.domain.base.CoroutinesContextFacade
import ai.sterling.kchat.domain.chat.model.ChatMessage
import ai.sterling.kchat.domain.chat.persistence.ChatDao
import ai.sterling.kchat.domain.chat.persistence.ChatStorage
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChatMessageStorage @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val chatDao: ChatDao,
    private val contextFacade: CoroutinesContextFacade
) : ChatStorage {

    override suspend fun insertChatMessages(messageList: List<ChatMessage>) = coroutineScope {
        withContext(contextFacade.io) {
            databaseManager.openDatabase()
            chatDao.insertAllMessages(messageList)
            databaseManager.closeDatabase()
        }
    }

    override suspend fun getChatMessage(id: Int): ChatMessage? = coroutineScope {
        withContext(contextFacade.io) {
            databaseManager.openDatabase()
            val chatMessage = chatDao.getChatMessage(id)
            databaseManager.closeDatabase()

            return@withContext chatMessage
        }
    }

    override suspend fun getAllChatMessages(): List<ChatMessage> = coroutineScope {
        withContext(contextFacade.io) {
            databaseManager.openDatabase()
            val chatList = chatDao.getAllMessages()
            databaseManager.closeDatabase()

            return@withContext chatList
        }
    }

    override suspend fun deleteAllMessages() = coroutineScope {
        withContext(contextFacade.io) {
            databaseManager.openDatabase()
            chatDao.deleteAllMessages()
            databaseManager.closeDatabase()
        }
    }
}
