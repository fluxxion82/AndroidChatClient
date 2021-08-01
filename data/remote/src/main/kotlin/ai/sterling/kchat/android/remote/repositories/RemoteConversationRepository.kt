package ai.sterling.kchat.android.remote.repositories

import ai.sterling.kchat.domain.base.CoroutinesContextFacade
import ai.sterling.kchat.domain.chat.model.Conversation
import ai.sterling.kchat.domain.chat.repository.ConversationRepository
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoteConversationRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val contextFacade: CoroutinesContextFacade
    // private val apiClient: ApiClient
): ConversationRepository {

    override fun getConversation(): Flow<Conversation> {
        TODO("Not yet implemented")
    }

    override suspend fun updateChatPayloads(payload: List<String>) {
        // "Update server with payloads"
        Log.d(TAG, "payload: ${payload.toTypedArray().contentToString()}")
        // apiClient.updateChatPayload(payload)
    }
}