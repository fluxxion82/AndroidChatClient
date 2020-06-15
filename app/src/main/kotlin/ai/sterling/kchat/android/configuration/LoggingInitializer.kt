package ai.sterling.kchat.android.configuration

import ai.sterling.kchat.domain.initialization.AppInitializer
import ai.sterling.logger.KLogger
import ai.sterling.logger.Logger
import javax.inject.Inject

internal class LoggingInitializer @Inject constructor(
    private val loggers: Set<@JvmSuppressWildcards Logger>
) : AppInitializer {

    override suspend fun initialize() =
        KLogger.registerLoggers(loggers)
}
