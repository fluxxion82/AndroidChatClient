package ai.sterling.kchat.android

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import ai.sterling.kchat.android.configuration.AppConfigurator
import ai.sterling.kchat.android.di.DaggerMainComponent
import ai.sterling.kchat.domain.base.invoke
import ai.sterling.kchat.domain.initialization.InitializeApplication
import dagger.Lazy
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class MainApplication : DaggerApplication() {

    @Inject
    internal lateinit var configurators: @JvmSuppressWildcards Set<AppConfigurator>

    @Inject
    lateinit var initializeApp: Lazy<InitializeApplication>

    override fun onCreate() {
        super.onCreate()
        runBlocking {
            withContext(Dispatchers.Default) {
                configurators.forEach {
                    launch { it.configure() }
                }
            }
            initializeApp.get().invoke()
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
        DaggerMainComponent.builder().create(this)
}
