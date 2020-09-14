package ai.sterling.logger.android.di

import ai.sterling.logger.android.AndroidLogger
import dagger.Module
import dagger.Provides

@Module
class AndroidLoggingModule {

    @Provides
    fun androidLogger() = AndroidLogger
}
