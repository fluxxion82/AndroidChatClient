package ai.sterling.kchat.android.di.modules

import ai.sterling.logging.Logger
import ai.sterling.logging.android.AndroidLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
class BuildConfigModule {

    @Provides
    @IntoSet
    internal fun androidLogger(): Logger = AndroidLogger
}