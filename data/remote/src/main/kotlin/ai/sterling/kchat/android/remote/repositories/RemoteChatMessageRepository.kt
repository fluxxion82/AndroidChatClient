package ai.sterling.kchat.android.remote.repositories

import ai.sterling.kchat.android.api.KChatApiClient
import ai.sterling.kchat.domain.base.CoroutineScopeFacade
import ai.sterling.kchat.domain.base.CoroutinesContextFacade
import ai.sterling.kchat.domain.base.model.Outcome
import ai.sterling.kchat.domain.chat.model.ChatMessage
import ai.sterling.kchat.domain.chat.model.ChatMessage.Companion.REPLY
import ai.sterling.kchat.domain.chat.persistence.ChatStorage
import ai.sterling.kchat.domain.chat.repository.ChatRespository
import ai.sterling.kchat.domain.exception.Failure
import ai.sterling.kchat.domain.user.persistences.UserPreferences
import ai.sterling.logger.KLogger
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteChatMessageRepository @Inject constructor(
    private val apiClient: KChatApiClient,
    private val chatStorage: ChatStorage,
    private val userPreferences: UserPreferences,
    private val contextFacade: CoroutinesContextFacade,
    private val contextScope: CoroutineScopeFacade
): ChatRespository {

    override fun getChatMessages(): ReceiveChannel<List<ChatMessage>> = contextScope.globalScope.produce {
        KLogger.d {
            "get chat messages"
        }

        send(chatStorage.getAllChatMessages())
        apiClient.receive().consumeEach { apiMsg ->
            KLogger.d {
                "receiving messages, getChat, msg= ${apiMsg.message}, type=${apiMsg.type}"
            }
            val msgList = listOf(apiMsg)
            chatStorage.insertChatMessages(msgList)
            send(chatStorage.getAllChatMessages())
        }
    }

    override suspend fun insertChatMessage(message: ChatMessage) = coroutineScope {
        KLogger.d {
            "insert/send chat messages"
        }
        withContext(contextFacade.io) {
            chatStorage.insertChatMessages(listOf(message))
            val msg = if (message.username == userPreferences.getServerInfo().username) {
                message.copy(type = REPLY)
            } else {
                message
            }
            apiClient.sendChatMessage(listOf(msg)).collect {
                KLogger.d {
                    "send message outcome: ${it.javaClass.name}"
                }
                when (it) {
                    is Outcome.Success -> {

                    }
                    is Outcome.Error -> {
                        when (it.cause) {
                            is Failure.NetworkConnection -> apiClient.connectToServer()
                            else -> {

                            }
                        }
                    }
                }
            }
        }
    }

    override suspend fun deleteAllChatMessages() = coroutineScope {
        withContext(contextFacade.io) {
            chatStorage.deleteAllMessages()
        }
    }
}
