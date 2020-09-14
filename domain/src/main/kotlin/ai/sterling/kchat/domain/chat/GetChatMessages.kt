package ai.sterling.kchat.domain.chat

import ai.sterling.kchat.domain.base.CoroutineScopeFacade
import ai.sterling.kchat.domain.base.Usecase
import ai.sterling.kchat.domain.chat.model.ChatMessage
import ai.sterling.kchat.domain.chat.repository.ChatRespository
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetChatMessages @Inject constructor(
    private val chatRepository: ChatRespository,
    private val contextScope: CoroutineScopeFacade
) : Usecase<Unit, ReceiveChannel<List<ChatMessage>>> {

    override suspend fun invoke(param: Unit): ReceiveChannel<List<ChatMessage>> {
        val broadcastChannel = BroadcastChannel<List<ChatMessage>>(1)
        contextScope.globalScope.launch {
            chatRepository.getChatMessages().consumeEach {
                println("chat messages from repo, size: ${it.size}")
                broadcastChannel.send(it)
            }
        }

        return broadcastChannel.openSubscription()

    }
}