package ai.sterling.kchat.android

import android.app.Application
import ai.sterling.kchat.domain.base.invoke
import ai.sterling.kchat.domain.initialization.InitializeApplication
import dagger.Lazy
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@HiltAndroidApp
class MainApplication : Application() {

    @Inject
    lateinit var initializeApp: Lazy<InitializeApplication>

    override fun onCreate() {
        super.onCreate()
        runBlocking {
//            withContext(Dispatchers.Default) {
//                configurators.forEach {
//                    launch { it.configure() }
//                }
//            }
            initializeApp.get().invoke()
        }
    }
}
