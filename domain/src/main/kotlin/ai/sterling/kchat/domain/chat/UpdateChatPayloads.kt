package ai.sterling.kchat.domain.chat

import ai.sterling.kchat.domain.base.Usecase
import ai.sterling.kchat.domain.chat.repository.ConversationRepository
import javax.inject.Inject
import javax.inject.Named

class UpdateChatPayloads @Inject constructor(
    @Named("remote") private val chatRepository: ConversationRepository
) : Usecase<List<String>, Unit> {

    override suspend fun invoke(param: List<String>) = chatRepository.updateChatPayloads(param)
}