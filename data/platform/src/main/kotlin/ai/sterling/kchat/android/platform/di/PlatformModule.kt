package ai.sterling.kchat.android.platform.di

import ai.sterling.kchat.android.platform.connectivity.AndroidConnectionMonitor
import ai.sterling.kchat.android.platform.repositories.LocalConversationRepository
import ai.sterling.kchat.android.platform.repositories.SharedUserPreferencesRepository
import ai.sterling.kchat.android.platform.service.ActivityProvider
import ai.sterling.kchat.domain.chat.repository.ConversationRepository
import ai.sterling.kchat.domain.connectivity.ConnectionMonitor
import ai.sterling.kchat.domain.initialization.AppInitializer
import ai.sterling.kchat.domain.user.persistences.UserPreferences
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
abstract class PlatformModule {

    @Binds
    @IntoSet
    internal abstract fun activityProvider(activityProvider: ActivityProvider): AppInitializer

    @Binds
    internal abstract fun userRepository(implementation: SharedUserPreferencesRepository): UserPreferences

    @Binds
    @Named("local")
    internal abstract fun conversationRepo(implementation: LocalConversationRepository): ConversationRepository

    @Binds
    internal abstract fun connectionMonitor(implementation: AndroidConnectionMonitor): ConnectionMonitor
}
