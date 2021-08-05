package ai.sterling.kchat.android.api

import ai.sterling.kchat.domain.base.CoroutineScopeFacade
import ai.sterling.kchat.domain.base.model.Outcome
import ai.sterling.kchat.domain.chat.exception.ChatFailure
import ai.sterling.kchat.domain.chat.model.ChatEvent
import ai.sterling.kchat.domain.chat.model.ChatMessage
import ai.sterling.kchat.domain.chat.persistence.ChatEventPersistence
import ai.sterling.kchat.domain.exception.Failure
import ai.sterling.kchat.domain.settings.models.ServerInfo
import ai.sterling.kchat.domain.user.persistences.UserPreferences
import ai.sterling.logging.KLogger
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.net.UnknownHostException
import javax.inject.Inject

class KChatApiClient @Inject constructor(
    private val chatEventPersistence: ChatEventPersistence,
    private val userPreferences: UserPreferences,
    private val contextScopeFacade: CoroutineScopeFacade
) {
    var socket: Socket? = null
    var printWriter: PrintWriter? = null
    private var bufferedReader: BufferedReader? = null
    private var serverInfo: ServerInfo? = null
    private var isConnected = false
    var webSocket: WebSocket? = null

//    init {
//        contextScopeFacade.globalScope.launch {
//            while (!isConnected) {
//                connectToServer().collect { outcome ->
//                    when(outcome) {
//                        is Outcome.Success -> {
//                            isConnected = true
//                        }
//                        is Outcome.Error -> {
//                            KLogger.e {
//                                "error, ${outcome.message}"
//                            }
//                            when(outcome.cause) {
//                                else -> {
//                                    disconnect()
//                                    delay(5000)
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

    fun isConnected() = isConnected

    suspend fun connect(): Outcome<Boolean> {
        var result: Outcome<Boolean> = Outcome.Error("", Failure.ServerError(500))
        serverInfo = userPreferences.getServerInfo()
        if (!serverInfo?.serverIP.isNullOrEmpty() && !serverInfo?.username.isNullOrEmpty()) {
            try {
                val okHttpClient = OkHttpClient.Builder().build()
                val request: Request = Request.Builder().url("ws://${serverInfo?.serverIP}:${serverInfo?.serverPort}/").build()
                //val listener = WSocketListener(serverInfo!!.username)
                webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
                    override fun onOpen(webSocket: WebSocket, response: Response) {
                        KLogger.d { "WebSocket connected" }

                        isConnected = true
                        val msg =
                            ChatMessage(
                                0,
                                serverInfo!!.username,
                                ChatMessage.LOGIN,
                                "",
                                Clock.System.now().toEpochMilliseconds()
                            )

                        webSocket.send(Gson().toJson(arrayListOf(msg)).toString())
                        KLogger.d { "send connect event" }
                        contextScopeFacade.globalScope.launch {
                            chatEventPersistence.update(ChatEvent.Connect)
                        }
                    }

                    override fun onMessage(webSocket: WebSocket, text: String) {
                        contextScopeFacade.globalScope.launch {
                            KLogger.d { "socket message: $text" }
                            var msg = Gson().fromJson(text, ChatMessage::class.java)
                            if (msg.type == ChatMessage.LOGIN) {
                                msg = msg.copy(message = "${msg.username} just joined")
                            } else if (msg.type == ChatMessage.LOGOUT) {
                                msg = msg.copy(message = "${msg.username} logged out")
                            }

                            chatEventPersistence.update(ChatEvent.MessageReceived(msg))
                        }
                    }

                    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {

                    }

                    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                        KLogger.d { "Closed: $code / $reason" }
                        disconnect()
                    }

                    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                        KLogger.d { "Error: ${t.message}" }
                        contextScopeFacade.globalScope.launch {
                            chatEventPersistence.update(ChatEvent.Error("Error: ${t.message}"))
                        }
                    }
                })
                //okHttpClient.dispatcher.executorService.shutdown()
                result = Outcome.Success(true)
            } catch (exception: IOException) {
                KLogger.e(exception) { "Failed to connect server ${serverInfo?.serverIP} on port ${serverInfo?.serverPort}" }
                disconnect()
                result = Outcome.Error("", ChatFailure.FailedToConnect(""))
            }
        }

        return result
    }

    suspend fun connectToServer(): Flow<Outcome<Boolean>> = channelFlow {
        serverInfo = userPreferences.getServerInfo()
        if (!serverInfo?.serverIP.isNullOrEmpty() && !serverInfo?.username.isNullOrEmpty()) {
            try {
                socket = Socket(serverInfo?.serverIP, serverInfo!!.serverPort) // Creating the server socket.
                if (socket != null) {
                    isConnected = true
                    printWriter = PrintWriter(socket!!.getOutputStream(), true)
                    val inputStreamReader = InputStreamReader(socket!!.getInputStream())
                    bufferedReader = BufferedReader(inputStreamReader)

                    /*
                     * just to send the username to server so we can display connected
                     * I wanted to just give this to the Sender and get the flush of queued
                     * messages, but managing the order is tricky
                     */
                    val msg =
                        ChatMessage(
                            0,
                            serverInfo!!.username,
                            ChatMessage.LOGIN,
                            "",
                            Clock.System.now().toEpochMilliseconds()
                        )
                    printWriter?.write(Gson().toJson(arrayListOf(msg)).toString() + "\n")
                    printWriter?.flush()
                    println("connected to server")
                    send(Outcome.Success(true))
                } else {
                    println("Server has not bean started on port ${serverInfo!!.serverPort}")
                    send(Outcome.Success(false))
                }
            } catch (exception: UnknownHostException) {
                KLogger.e(exception) { "Failed to connect server ${serverInfo?.serverIP} on port ${serverInfo?.serverPort}" }
                send(Outcome.Error(exception.message ?: "failed to connect", Failure.NetworkConnection()))
            } catch (exception: IOException) {
                KLogger.e(exception) { "Failed to connect server ${serverInfo?.serverIP} on port ${serverInfo?.serverPort}" }
                send(Outcome.Error(exception.message ?: "failed to connect", Failure.NetworkConnection()))
            }
        }
    }

    suspend fun sendChatMessage(messageList: List<ChatMessage>): Flow<Outcome<Boolean>> = channelFlow {
        if (!isConnected) {
            KLogger.i { "not connected" }
            send(Outcome.Error("socket not available", Failure.NetworkConnection()))
        } else if (messageList.isNotEmpty()) {
            KLogger.d {
                "sending messages"
            }
            val messageBuilder = StringBuilder()
            try {
                val element = Gson().toJsonTree(
                    messageList,
                    object : TypeToken<List<ChatMessage?>?>() {}.type
                )
                messageBuilder.append(element.asJsonArray)
                messageBuilder.append("\n") //automatic flushing is enabled on PrintWriter

                webSocket!!.send(messageBuilder.toString())

                //printWriter?.write(messageBuilder.toString())
                //printWriter?.flush()
                contextScopeFacade.globalScope.launch {
                    messageList.forEach {
                        KLogger.d { "sent  message, type: ${it.type} : $it" }
                        chatEventPersistence.update(ChatEvent.MessageSent(it))
                    }
                }
                send(Outcome.Success(true))
            } catch (exception: Exception) {
                send(Outcome.Error(exception.message ?: "failed to write out messages", ChatFailure.SendChatMessageFailure))
            }
        }
    }

    fun disconnect() {
        KLogger.d { "disconnect" }
        if (!isConnected) {
            return
        }

        try {
            // Send logout message
            val msg = ChatMessage(
                0,
                serverInfo!!.username,
                ChatMessage.LOGOUT,
                "",
                Clock.System.now().toEpochMilliseconds()
            )
            KLogger.d {
                "log out"
            }
            webSocket?.send(Gson().toJson(arrayListOf(msg)).toString())
        } catch (e: Exception) {
            KLogger.e(e) {
                "exception, ${e.message}"
            }
        }

        try {
            webSocket?.close(10000, null)
        } catch (e: Exception) {
            KLogger.e(e) {
                "exception, ${e.message}"
            }
        }

        isConnected = false

        contextScopeFacade.globalScope.launch {
            chatEventPersistence.update(ChatEvent.Disconnect)
        }
    }
}
