package ai.sterling.kchat.domain.chat.model

import com.soywiz.klock.DateFormat
import com.soywiz.klock.format

data class ChatMessage(val id: Int, val username: String, val type: Int, var message: String, val date: Long) {

    override fun toString(): String {
        return when (type) {
            LOGIN -> {
                message = "$username just joined \n"
                message
            }

            LOGOUT -> {
                message = "$username has logged out.\n"
                message
            }

            REPLY, MESSAGE -> {
                // this is different from the android client
                val simpleDateFormat = DateFormat("HH:mm:ss")
                val time = simpleDateFormat.format(date)
                "$time $username: $message \n"
            }

            else -> "Invalid operation.\n"
        }
    }

    companion object {
        var MESSAGE = 0
        var REPLY = 1
        var LOGOUT = 2
        var LOGIN = 3
    }
}