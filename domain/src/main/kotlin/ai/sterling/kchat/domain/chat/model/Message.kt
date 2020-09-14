package ai.sterling.kchat.domain.chat.model

data class Message (
    val id: String,
    val text: String,
    val payload: String,
    val route: String,
    val tag: String,
    val reply: Boolean,
    val mine: Boolean = false
)
