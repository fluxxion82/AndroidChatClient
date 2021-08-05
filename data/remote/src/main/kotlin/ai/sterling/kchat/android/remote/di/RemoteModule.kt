package ai.sterling.kchat.android.remote.di

import ai.sterling.kchat.android.remote.repositories.RemoteChatMessageRepository
import ai.sterling.kchat.android.remote.repositories.RemoteConversationRepository
import ai.sterling.kchat.android.remote.repositories.RemoteUserRepository
import ai.sterling.kchat.domain.chat.repository.ChatRepository
import ai.sterling.kchat.domain.chat.repository.ConversationRepository
import ai.sterling.kchat.domain.user.persistences.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteModule {

    @Binds
    @Named("remote")
    internal abstract fun conversationRepository(implementation: RemoteConversationRepository): ConversationRepository

    @Binds
    internal abstract fun chatRepository(implementation: RemoteChatMessageRepository): ChatRepository

    @Binds
    internal abstract fun userRepository(implementation: RemoteUserRepository): UserRepository
}
