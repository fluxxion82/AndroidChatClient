package ai.sterling.kchat.domain.chat.model

data class Conversation(val messages: Map<String, List<Message>>) {
    fun findMessageRoute(route: String): Map<String, List<Message>> {
        return messages.filter {
            it.key == route
        }
    }

    fun getStartMessages(): List<Message> {
        return messages.values.filter {
            it.find { message ->
                message.tag.contains("start")
            } != null
        }.flatten()
    }
}