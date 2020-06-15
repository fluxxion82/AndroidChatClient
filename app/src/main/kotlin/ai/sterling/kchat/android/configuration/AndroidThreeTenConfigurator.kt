package ai.sterling.kchat.android.configuration

import android.app.Application
import javax.inject.Inject

internal class AndroidThreeTenConfigurator @Inject constructor(
    private val application: Application
) : AppConfigurator {

    override suspend fun configure() = Unit
        //AndroidThreeTen.init(application)
}
