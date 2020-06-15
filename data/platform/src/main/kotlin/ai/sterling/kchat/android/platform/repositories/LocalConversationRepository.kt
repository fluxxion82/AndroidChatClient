package ai.sterling.kchat.android.platform.repositories

import ai.sterling.kchat.android.platform.R
import ai.sterling.kchat.domain.base.CoroutinesContextFacade
import ai.sterling.kchat.domain.chat.model.Conversation
import ai.sterling.kchat.domain.chat.model.Message
import ai.sterling.kchat.domain.chat.repository.ConversationRepository
import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

class LocalConversationRepository @Inject constructor(
    private val context: Context,
    private val contextFacade: CoroutinesContextFacade
): ConversationRepository {

    override fun getConversation(): Flow<Conversation>  = channelFlow {
        val jsonString = getJsonString(R.raw.allornothing)

        val gsonBuilder = GsonBuilder();
        val deserializer = MessageDeserializer()
        gsonBuilder.registerTypeAdapter(Message::class.java, deserializer)

        val gson = gsonBuilder.create()

        val mapType = object : TypeToken<Map<String, Message>>() {}.type
        val messageMap = gson.fromJson<Map<String, List<Message>>>(jsonString, mapType)

        send(Conversation(messageMap))
    }.flowOn(contextFacade.io)

    override suspend fun updateChatPayloads(payload: List<String>) {
        // "Update server with payloads"
        //Log.d(TAG, "payload: ${payload.toTypedArray().contentToString()}")
    }

    private fun getJsonString(resource: Int): String {
        val inputStream: InputStream = context.resources.openRawResource(resource)

        val outputStream = ByteArrayOutputStream()

        val buf = ByteArray(1024)
        var len: Int
        try {
            while (inputStream.read(buf).also { len = it } != -1) {
                outputStream.write(buf, 0, len)
            }
        } catch (e: IOException) {
        }

        return outputStream.toString()
    }

    companion object {
        val TAG = LocalConversationRepository::class.simpleName
    }
}