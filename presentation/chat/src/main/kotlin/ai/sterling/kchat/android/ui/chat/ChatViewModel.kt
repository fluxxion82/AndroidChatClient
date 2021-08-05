package ai.sterling.kchat.android.ui.chat

import ai.sterling.kchat.android.ui.base.BaseViewModel
import ai.sterling.kchat.domain.base.invoke
import ai.sterling.kchat.domain.chat.SendChatMessage
import ai.sterling.kchat.domain.chat.GetChatMessages
import ai.sterling.kchat.domain.chat.model.ChatMessage
import ai.sterling.kchat.domain.settings.GetServerInfo
import ai.sterling.kchat.domain.settings.models.ServerInfo
import ai.sterling.kchat.multicore.model.Platform
import ai.sterling.kchat.multicore.model.Sample
import ai.sterling.logging.KLogger
import android.os.Build
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getChatMessages: GetChatMessages,
    private val sendChatMessage: SendChatMessage,
    private val getServerInfo: GetServerInfo
) : BaseViewModel() {
    val messages = MutableStateFlow(
        listOf(
            ChatMessage(
                Sample().checkMe(),
                Platform.name(),
                ChatMessage.REPLY,
                "Hello from ${Platform.name()}",
                System.currentTimeMillis()
            )
        )
    )

    lateinit var username: String
    lateinit var serverInfo: ServerInfo

    init {
        launch {
            getServerInfo().collect {
                serverInfo = it
                username = it.username
            }
        }
        launch {
            getChatMessages().collect {
                messages.value += it
            }
        }
    }

    fun onSendMessage(message: String) {
        KLogger.d {
            "reply clicked"
        }

        launch {
            sendChatMessage(
                ChatMessage(
                    0,
                    username,
                    ChatMessage.MESSAGE,
                    message,
                    Clock.System.now().toEpochMilliseconds()
                )
            )
        }
    }
}