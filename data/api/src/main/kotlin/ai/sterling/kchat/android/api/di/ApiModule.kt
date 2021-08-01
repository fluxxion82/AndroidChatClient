package ai.sterling.kchat.android.api.di

import ai.sterling.kchat.android.api.KChatApiClient
import ai.sterling.kchat.domain.base.CoroutineScopeFacade
import ai.sterling.kchat.domain.user.persistences.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    @Reusable
    internal fun provideKChatApi(
        userPreferences: UserPreferences,
        contextScopeFacade: CoroutineScopeFacade) = KChatApiClient(userPreferences, contextScopeFacade)
}
