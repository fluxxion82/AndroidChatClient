package ai.sterling.kchat.chat

import ai.sterling.kchat.BasePresentationTest
import ai.sterling.kchat.android.ui.chat.ChatViewModel
import ai.sterling.kchat.domain.chat.GetConversation
import ai.sterling.kchat.domain.chat.UpdateChatPayloads
import ai.sterling.kchat.domain.chat.model.Conversation
import ai.sterling.kchat.domain.chat.model.Message
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class ChatViewModelTest : BasePresentationTest() {

    lateinit var getConversationUseCase: GetConversation
    lateinit var updatePayloadUseCase: UpdateChatPayloads
    lateinit var channel: Channel<Conversation>
    lateinit var viewModel: ChatViewModel

    @Before
    fun setUp() {
        channel = Channel()
        getConversationUseCase = mock {
            onBlocking { invoke(Unit) } doReturn channel.consumeAsFlow()
        }
        updatePayloadUseCase = mock()
    }

    @Test
    fun `observes conversation changes`() {
        viewModel = runBlocking { ChatViewModel(getConversationUseCase, updatePayloadUseCase) }

        channel.sendBlocking(
            Conversation(
                mapOf(
                    Pair("ID1", getMessages("ID1", "ID1")),
                    Pair("ID2", getMessages("ID2", "ID2"))
                )
            )
        )
        assertThat(viewModel.conversation.messages).isEqualTo(mapOf(
            Pair("ID1", getMessages("ID1", "ID1")),
            Pair("ID2", getMessages("ID2", "ID2"))
        ))
        channel.sendBlocking(
            Conversation(
                mapOf(
                    Pair("ID5", getMessages("ID5", "ID5")),
                    Pair("ID6", getMessages("ID6", "ID6"))
                )
            )
        )
        assertThat(viewModel.conversation.messages).isEqualTo(mapOf(
            Pair("ID5", getMessages("ID5", "ID5")),
            Pair("ID6", getMessages("ID6", "ID6"))
        ))
    }

    private fun getMessages(vararg ids: String): List<Message> = ids.mapIndexed { index, id ->
        Message(
            id,
            "Test message",
            "",
            "ID${index + 2}",
            "",
            reply = false,
            mine = false
        )

    }
}
