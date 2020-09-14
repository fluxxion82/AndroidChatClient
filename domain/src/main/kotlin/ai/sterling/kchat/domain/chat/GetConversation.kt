package ai.sterling.kchat.domain.chat

import ai.sterling.kchat.domain.base.Usecase
import ai.sterling.kchat.domain.chat.model.Conversation
import ai.sterling.kchat.domain.chat.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Named

class GetConversation @Inject constructor(
    @Named("local") private val chatRepository: ConversationRepository
) : Usecase<Unit, Flow<Conversation>> {

    override suspend fun invoke(param: Unit): Flow<Conversation> = chatRepository.getConversation()
}