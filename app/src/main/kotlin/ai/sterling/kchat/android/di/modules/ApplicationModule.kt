package ai.sterling.kchat.android.di.modules

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.multibindings.IntoSet
import ai.sterling.kchat.android.MainApplication
import ai.sterling.kchat.android.configuration.AndroidThreeTenConfigurator
import ai.sterling.kchat.android.configuration.AppConfigurator
import ai.sterling.kchat.android.configuration.LoggingInitializer
import ai.sterling.kchat.domain.initialization.AppInitializer
import ai.sterling.logger.Logger
import ai.sterling.logger.android.AndroidLogger
import dagger.Module
import dagger.Provides

@Module
internal abstract class ApplicationModule {

    @Binds
    abstract fun context(application: MainApplication): Context

    @Binds
    abstract fun application(application: MainApplication): Application

    @Binds
    @IntoSet
    abstract fun androidThreeTen(dateConfigurator: AndroidThreeTenConfigurator): AppConfigurator

    @Binds
    @IntoSet
    abstract fun loggingInitializer(logging: LoggingInitializer): AppInitializer
}
