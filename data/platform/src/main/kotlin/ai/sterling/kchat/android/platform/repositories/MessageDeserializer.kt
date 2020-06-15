package ai.sterling.kchat.android.platform.repositories

import ai.sterling.kchat.domain.chat.model.Message
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.reflect.Type

class MessageDeserializer : JsonDeserializer<List<Message>> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): List<Message> {
        val messageList = mutableListOf<Message>()
        val jsonObject: JsonObject = json.asJsonObject

        val textString = jsonObject.get("text").asString
        val textArray = textString.split("|")

        textArray.forEach {
            messageList.add(
                Message(
                    jsonObject.get("id").asString,
                    it,
                    "",
                    "",
                    jsonObject.get("tag").asString,
                    false
                )
            )
        }

        val replyElement = jsonObject.get("replies")
        val payloadElement = jsonObject.get("payloads")
        val routeElement = jsonObject.get("routes")

        if(replyElement.isJsonArray) {
            val replyArray = replyElement.asJsonArray!!
            val payloadArray = payloadElement.asJsonArray!!
            val routeArray = routeElement.asJsonArray!!

            for (i in 0 until replyArray.size()) {
                messageList.add(
                    Message(
                        jsonObject.get("id").asString,
                        replyArray[i].asString!!,
                        payloadArray[i].asString!!,
                        routeArray[i].asString!!,
                        jsonObject.get("tag").asString,
                        true
                    )
                )
            }
        } else {
            messageList.add(
                Message(
                    jsonObject.get("id").asString,
                    replyElement.asString!!,
                    payloadElement.asString!!,
                    routeElement.asString!!,
                    jsonObject.get("tag").asString,
                    true
                )
            )
        }

        return messageList
    }
}