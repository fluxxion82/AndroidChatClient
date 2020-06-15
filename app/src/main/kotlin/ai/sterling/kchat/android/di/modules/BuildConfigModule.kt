package ai.sterling.kchat.android.di.modules

import ai.sterling.logger.Logger
import ai.sterling.logger.android.AndroidLogger
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet

@Module
class BuildConfigModule {

    @Provides
    @IntoSet
    internal fun androidLogger(): Logger = AndroidLogger
}