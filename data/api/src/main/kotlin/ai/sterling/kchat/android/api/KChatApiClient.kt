package ai.sterling.kchat.android.api

import ai.sterling.kchat.domain.base.CoroutineScopeFacade
import ai.sterling.kchat.domain.base.model.Outcome
import ai.sterling.kchat.domain.chat.exception.ChatMessageFailure
import ai.sterling.kchat.domain.chat.model.ChatMessage
import ai.sterling.kchat.domain.exception.Failure
import ai.sterling.kchat.domain.settings.models.ServerInfo
import ai.sterling.kchat.domain.user.persistences.UserPreferences
import ai.sterling.logger.KLogger
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.soywiz.klock.DateTime
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
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
    private val userPreferences: UserPreferences,
    private val contextScopeFacade: CoroutineScopeFacade
) {
    var socket: Socket? = null
    var printWriter: PrintWriter? = null
    private var bufferedReader: BufferedReader? = null
    private var serverInfo: ServerInfo? = null
    private var isConnected = false
    var webSocket: WebSocket? = null

    private val broadcastChannel = ConflatedBroadcastChannel<ChatMessage>()

    init {
        contextScopeFacade.globalScope.launch {
            while (!isConnected) {
                connectToServer().collect { outcome ->
                    when(outcome) {
                        is Outcome.Success -> {
                            isConnected = true
                        }
                        is Outcome.Error -> {
                            KLogger.e {
                                "error, ${outcome.message}"
                            }
                            when(outcome.cause) {
                                else -> {
                                    disconnect()
                                    delay(1000)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    suspend fun connect(): Flow<Outcome<Boolean>> {
        val channel = BroadcastChannel<Outcome<Boolean>>(1)
        serverInfo = userPreferences.getServerInfo()
        if (!serverInfo?.serverIP.isNullOrEmpty() && !serverInfo?.username.isNullOrEmpty()) {
            try {
                val okHttpClient = OkHttpClient.Builder().build()
                val request: Request = Request.Builder().url("ws://${serverInfo!!.serverIP}:${serverInfo!!.serverPort}/").build()
                //val listener = WSocketListener(serverInfo!!.username)
                webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
                    override fun onOpen(webSocket: WebSocket, response: Response) {
                        KLogger.d {
                            "WebSocket connected"
                        }

                        val msg =
                            ChatMessage(
                                0,
                                serverInfo!!.username,
                                ChatMessage.LOGIN,
                                "",
                                DateTime.nowUnixLong()
                            )

                        webSocket.send(Gson().toJson(arrayListOf(msg)).toString())
                        contextScopeFacade.globalScope.launch {
                            channel.send(Outcome.Success(true))
                        }
                    }

                    override fun onMessage(webSocket: WebSocket, text: String) {
                        contextScopeFacade.globalScope.launch {
                            KLogger.d {
                                "socket message: $text"
                            }
                            var msg = Gson().fromJson(text, ChatMessage::class.java)
                            if (msg.type == ChatMessage.LOGIN) {
                                msg = msg.copy(message = "${msg.username} just joined")
                            } else if (msg.type == ChatMessage.LOGOUT) {
                                msg = msg.copy(message = "${msg.username} logged out")
                            }

                            broadcastChannel.send(msg)
                        }
                    }

                    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {

                    }

                    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                        contextScopeFacade.globalScope.launch {
                            disconnect()
                            KLogger.d {
                                "Closed: $code / $reason"
                            }
                        }
                    }

                    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                        KLogger.d {
                            "Error: ${t.message}"
                        }
                        contextScopeFacade.globalScope.launch {
                            channel.send(Outcome.Error(t.message ?: "failed to connect", Failure.NetworkConnection()))
                        }
                    }
                })
                //okHttpClient.dispatcher.executorService.shutdown()
            } catch (exception: IOException) {
                println("Faild to connect server ${serverInfo?.serverIP} on port ${serverInfo?.serverPort}")
                disconnect()
                channel.send(Outcome.Error(exception.message ?: "failed to write out messages", Failure.NetworkConnection()))
            }
        }

        return channel.openSubscription().consumeAsFlow()
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
                            DateTime.nowUnixLong()
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
                println("Faild to connect server ${serverInfo?.serverIP} on port ${serverInfo?.serverPort}")
                send(Outcome.Error(exception.message ?: "failed to write out messages", Failure.NetworkConnection()))
            } catch (exception: IOException) {
                println("Faild to connect server ${serverInfo?.serverIP} on port ${serverInfo?.serverPort}")
                send(Outcome.Error(exception.message ?: "failed to write out messages", Failure.NetworkConnection()))
            }
        }
    }

    suspend fun sendChatMessage(messageList: List<ChatMessage>): Flow<Outcome<Boolean>> = channelFlow {
        if (!isConnected) {
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
                send(Outcome.Success(true))
            } catch (exception: Exception) {
                send(Outcome.Error(exception.message ?: "failed to write out messages", ChatMessageFailure.SendChatMessageFailure()))
            }
        }
    }

    fun receive(): ReceiveChannel<ChatMessage> = broadcastChannel.openSubscription()
//        contextScopeFacade.globalScope.produce<ChatMessage> {
//        while (isConnected) {
//            try {
//                if (bufferedReader?.ready() == true) {
//                    val message = bufferedReader?.readLine()
//                    KLogger.d {
//                        "receiving messages:, $message"
//                    }
//                    var msg = Gson().fromJson(message, ChatMessage::class.java)
//                    if (msg.type == ChatMessage.LOGIN) {
//                        msg  = msg.copy(message = "${msg.username} just joined")
//                    } else if (msg.type == ChatMessage.LOGOUT) {
//                        msg = msg.copy(message = "${msg.username} logged out")
//                    }
//                    send(msg)
//                }
//            } catch (e: UnknownHostException) {
//                e.printStackTrace()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            } catch (exception: JsonSyntaxException) {
//                exception.printStackTrace()
//            }
//            delay(500)
//        }
//    }

    suspend fun disconnect() {
        KLogger.d {
            "disconnect"
        }
        try {
            //if (printWriter != null) {
                // Send logout message
                val msg = ChatMessage(
                    0,
                    serverInfo!!.username,
                    ChatMessage.LOGOUT,
                    "",
                    DateTime.nowUnixLong()
                )
                KLogger.d {
                    "loggggout"
                }
                webSocket?.send(Gson().toJson(arrayListOf(msg)).toString())
//                printWriter?.write(Gson().toJson(msg).toString() + "\n")//automatic flushing is enabled on PrintWriter
//                printWriter?.flush()
//
//                printWriter?.close()
            //}
        } catch (e: java.lang.Exception) {
            //not much else to do
            KLogger.e(e) {
                "exception, ${e.message}"
            }
        }

        try {
            webSocket?.close(10000, null)
            //socket?.close()
        } catch (e: java.lang.Exception) {
            //not much else to do
        }

        try {
            //bufferedReader?.close()
        } catch (e: java.lang.Exception) {
            //not much else to do
        }

        isConnected = false
    }
}
