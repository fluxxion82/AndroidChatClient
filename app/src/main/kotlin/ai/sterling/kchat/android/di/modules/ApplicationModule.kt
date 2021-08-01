package ai.sterling.kchat.android.di.modules

import ai.sterling.kchat.android.configuration.LoggingInitializer
import ai.sterling.kchat.domain.initialization.AppInitializer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ApplicationModule {

    @Binds
    @IntoSet
    abstract fun loggingInitializer(logging: LoggingInitializer): AppInitializer
}
