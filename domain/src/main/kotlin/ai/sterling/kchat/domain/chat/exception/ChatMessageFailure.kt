package ai.sterling.kchat.domain.chat.exception

import ai.sterling.kchat.domain.exception.Failure

class ChatMessageFailure {
    class GeneralChatMessageFailure : Failure.FeatureFailure()
    class SendChatMessageFailure : Failure.FeatureFailure()
}
