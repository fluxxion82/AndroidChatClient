package ai.sterling.kchat.android.di

import ai.sterling.kchat.android.remote.di.RemoteModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import ai.sterling.kchat.android.MainApplication
import ai.sterling.kchat.android.api.di.ApiModule
import ai.sterling.kchat.android.database.di.DatabaseModule
import ai.sterling.kchat.android.database.di.StorageModule
import ai.sterling.kchat.android.di.modules.ApplicationModule
import ai.sterling.kchat.android.di.modules.BuildConfigModule
import ai.sterling.kchat.android.platform.di.PlatformModule
import ai.sterling.kchat.domain.di.DomainModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ApplicationModule::class,
        TestInjectors::class,
        DomainModule::class,
        RemoteModule::class,
        PlatformModule::class,
        DatabaseModule::class,
        StorageModule::class,
        ApiModule::class,
        BuildConfigModule::class
    ]
)
interface MainComponent : AndroidInjector<MainApplication> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<MainApplication>()
}
