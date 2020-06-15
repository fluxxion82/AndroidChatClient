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
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.net.UnknownHostException
import java.util.Calendar
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

    init {
        contextScopeFacade.globalScope.launch {
            while (!isConnected) {
                connectToServer().collect { outcome ->
                    when (outcome) {
                        is Outcome.Success -> {
                            isConnected = true
                        }
                        is Outcome.Error -> {
                            when (outcome.cause) {
                                else -> {
                                    disconnect()
                                    delay(500)
                                }
                            }
                        }
                    }
                }

            }
        }
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
                            Calendar.getInstance().timeInMillis
                        )
                    printWriter?.write(Gson().toJson(msg).toString() + "\n")
                    printWriter?.flush()
                } else {
                    println("Server has not bean started on port ${serverInfo!!.serverPort}")
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

    suspend fun disconnect() {
        KLogger.d {
            "disconnect"
        }
        try {
            if (printWriter != null) {
                // Send logout message
                val msg = ChatMessage(
                    0,
                    serverInfo!!.username,
                    ChatMessage.LOGOUT,
                    "",
                    Calendar.getInstance().timeInMillis
                )
                KLogger.d {
                    "loggggout"
                }
                printWriter?.write(Gson().toJson(msg).toString() + "\n")//automatic flushing is enabled on PrintWriter
                printWriter?.flush()

                printWriter?.close()
            }
        } catch (e: java.lang.Exception) {
            //not much else to do
            KLogger.e(e) {
                "exception, ${e.message}"
            }
        }

        try {
            socket?.close()
        } catch (e: java.lang.Exception) {
            //not much else to do
        }

        try {
            bufferedReader?.close()
        } catch (e: java.lang.Exception) {
            //not much else to do
        }

        isConnected = false
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
                printWriter?.write(messageBuilder.toString())
                printWriter?.flush()
                send(Outcome.Success(true))
            } catch (exception: Exception) {
                send(Outcome.Error(exception.message ?: "failed to write out messages", ChatMessageFailure.SendChatMessageFailure()))
            }
        }
    }

    fun receive(): ReceiveChannel<ChatMessage> = contextScopeFacade.globalScope.produce<ChatMessage> {
        while (isConnected) {
            try {
                if (bufferedReader?.ready() == true) {
                    val message = bufferedReader?.readLine()
                    KLogger.d {
                        "receiving messages"
                    }
                    var msg = Gson().fromJson(message, ChatMessage::class.java)
                    if (msg.type == ChatMessage.LOGIN) {
                        msg  = msg.copy(message="${msg.username} just joined")
                    } else if (msg.type == ChatMessage.LOGOUT) {
                        msg = msg.copy(message="${msg.username} logged out")
                    }
                    send(msg)
                }
            } catch (e: UnknownHostException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            delay(500)
        }
    }
}
