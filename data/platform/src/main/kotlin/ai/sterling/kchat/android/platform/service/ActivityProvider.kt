package ai.sterling.kchat.android.platform.service

import ai.sterling.kchat.domain.app.persistence.ForegroundEventPersistence
import ai.sterling.kchat.domain.base.CoroutineScopeFacade
import ai.sterling.kchat.domain.initialization.AppInitializer
import android.app.Activity
import android.app.Application
import android.os.Bundle
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class ActivityProvider @Inject constructor(
    private val application: Application,
    private val coroutinesScopeFacade: CoroutineScopeFacade,
    private val foregroundEventPersistence: ForegroundEventPersistence
) : AppInitializer {

    private var activityOnTop: Activity? = null

    override suspend fun initialize() {
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {
                activityOnTop = null
                coroutinesScopeFacade.globalScope.launch {
                    foregroundEventPersistence.update(false)
                }
            }

            override fun onActivityResumed(activity: Activity) {
                activityOnTop = activity
                coroutinesScopeFacade.globalScope.launch {
                    foregroundEventPersistence.update(true)
                }
            }

            override fun onActivityStarted(activity: Activity) = Unit

            override fun onActivityDestroyed(activity: Activity) = Unit

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

            override fun onActivityStopped(activity: Activity) = Unit

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
        })
    }

    fun get(): Activity? = activityOnTop
}
