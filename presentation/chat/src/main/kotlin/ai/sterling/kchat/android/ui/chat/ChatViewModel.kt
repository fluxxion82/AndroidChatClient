package ai.sterling.kchat.android.ui.chat

import ai.sterling.kchat.android.ui.base.BaseViewModel
import ai.sterling.kchat.domain.base.invoke
import ai.sterling.kchat.domain.chat.AddChatMessage
import ai.sterling.kchat.domain.chat.GetChatMessages
import ai.sterling.kchat.domain.chat.model.ChatMessage
import ai.sterling.kchat.domain.settings.GetServerInfo
import ai.sterling.kchat.domain.settings.models.ServerInfo
import ai.sterling.logger.KLogger
import android.database.sqlite.SQLiteDatabase
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import com.soywiz.klock.DateTime
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.net.UnknownHostException
import java.util.ArrayList
import java.util.Calendar
import javax.inject.Inject

class ChatViewModel @Inject constructor(
    private val getChatMessages: GetChatMessages,
    private val addChatMessage: AddChatMessage,
    private val getServerInfo: GetServerInfo
) : BaseViewModel() {
    val messages = MutableLiveData<MutableList<ChatMessage>>()
    val reply = MutableLiveData<String>()
    lateinit var username: String
    lateinit var serverInfo: ServerInfo
    private var chatJob: Job? = null
    private var replyJob: Job? = null

    init {
        launch {
            getServerInfo().collect {
                serverInfo = it
                username = it.username
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        chatJob = launch {
            getChatMessages().consumeEach {
                KLogger.d {
                    "get messages, on resume, size: ${it.size}"
                }
                messages.value = it.toMutableList()
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        chatJob?.cancel()
    }

    fun replyClicked() {
        KLogger.d {
            "reply clicked"
        }
        if (!reply.value.isNullOrEmpty()) {
            replyJob = launch {
                addChatMessage(
                    ChatMessage(
                        0,
                        username,
                        ChatMessage.MESSAGE,
                        reply.value.toString(),
                        DateTime.now().unixMillisLong
                    )
                )

                reply.value = ""

                getChatMessages().receiveAsFlow().collect {
                    KLogger.d {
                        "get messages, reply clicked, size: ${it.size}"
                    }
                    messages.value = it.toMutableList()
                    //messages.postValue(messages.value)
                }

                replyJob?.cancel()
            }
        }
    }
}