package ai.sterling.kchat.domain.chat

import ai.sterling.kchat.domain.base.Usecase
import ai.sterling.kchat.domain.chat.model.ChatMessage
import ai.sterling.kchat.domain.chat.repository.ChatRespository
import javax.inject.Inject

class AddChatMessage @Inject constructor(
    private val chatRepository: ChatRespository
) : Usecase<ChatMessage, Unit> {

    override suspend fun invoke(param: ChatMessage) = chatRepository.insertChatMessage(param)
}