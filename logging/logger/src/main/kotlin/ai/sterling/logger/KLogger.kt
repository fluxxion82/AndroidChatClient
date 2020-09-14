@file:JvmName("KChatLogger")

package ai.sterling.logger

import ai.sterling.logger.Logger.Priority
import java.util.regex.Matcher
import java.util.regex.Pattern

@Suppress("NOTHING_TO_INLINE")
object KLogger {

    private const val CALL_STACK_INDEX = 3 // Depth of `buildTag` method
    private val ANONYMOUS_CLASS_PATTERN by lazy { Pattern.compile("(\\$\\d+)+$") }

    private var properties = ThreadLocal<Map<String, String>>()
    private val loggers = mutableListOf<Logger>()
    private val explicitTag = ThreadLocal<String?>()

    fun registerLoggers(logger: Logger, vararg other: Logger) {
        synchronized(loggers) {
            loggers.add(logger)
            loggers.addAll(other)
        }
    }

    fun registerLoggers(loggers: Collection<Logger>) {
        synchronized(KLogger.loggers) {
            KLogger.loggers.addAll(loggers)
        }
    }

    fun unregisterLoggers(logger: Logger, vararg other: Logger) {
        synchronized(loggers) {
            loggers.remove(logger)
            loggers.removeAll(other)
        }
    }

    fun unregisterLoggers(loggers: Collection<Logger>) {
        synchronized(KLogger.loggers) {
            KLogger.loggers.removeAll(loggers)
        }
    }

    @JvmStatic
    fun v(message: () -> String) {
        performLog(
            Priority.VERBOSE,
            message = message,
            throwable = null
        )
    }

    @JvmStatic
    fun v(throwable: Throwable, message: () -> String) {
        performLog(
            Priority.VERBOSE,
            throwable = throwable,
            message = message
        )
    }

    @JvmStatic
    fun v(throwable: Throwable) {
        performLog(
            Priority.VERBOSE,
            message = null,
            throwable = throwable
        )
    }

    @JvmStatic
    fun d(message: () -> String) {
        performLog(
            Priority.DEBUG,
            message = message,
            throwable = null
        )
    }

    @JvmStatic
    fun d(throwable: Throwable, message: () -> String) {
        performLog(
            Priority.DEBUG,
            throwable = throwable,
            message = message
        )
    }

    @JvmStatic
    fun d(throwable: Throwable) {
        performLog(
            Priority.DEBUG,
            message = null,
            throwable = throwable
        )
    }

    @JvmStatic
    fun i(message: () -> String) {
        performLog(Priority.INFO, message = message, throwable = null)
    }

    @JvmStatic
    fun i(throwable: Throwable, message: () -> String) {
        performLog(
            Priority.INFO,
            throwable = throwable,
            message = message
        )
    }

    @JvmStatic
    fun i(throwable: Throwable) {
        performLog(
            Priority.INFO,
            message = null,
            throwable = throwable
        )
    }

    @JvmStatic
    fun w(message: () -> String) {
        performLog(
            Priority.WARNING,
            message = message,
            throwable = null
        )
    }

    @JvmStatic
    fun w(throwable: Throwable, message: () -> String) {
        performLog(
            Priority.WARNING,
            throwable = throwable,
            message = message
        )
    }

    @JvmStatic
    fun w(throwable: Throwable) {
        performLog(
            Priority.WARNING,
            message = null,
            throwable = throwable
        )
    }

    @JvmStatic
    fun e(message: () -> String) {
        performLog(
            Priority.ERROR,
            message = message,
            throwable = null
        )
    }

    @JvmStatic
    fun e(throwable: Throwable, message: () -> String) {
        performLog(
            Priority.ERROR,
            throwable = throwable,
            message = message
        )
    }

    @JvmStatic
    fun e(throwable: Throwable) {
        performLog(
            Priority.ERROR,
            message = null,
            throwable = throwable
        )
    }

    @JvmStatic
    fun wtf(message: () -> String) {
        performLog(Priority.WTF, message = message, throwable = null)
    }

    @JvmStatic
    fun wtf(throwable: Throwable, message: () -> String) {
        performLog(
            Priority.WTF,
            throwable = throwable,
            message = message
        )
    }

    @JvmStatic
    fun wtf(throwable: Throwable) {
        performLog(
            Priority.WTF,
            message = null,
            throwable = throwable
        )
    }

    @JvmStatic
    fun log(priority: Priority, message: () -> String) {
        performLog(priority, message = message, throwable = null)
    }

    @JvmStatic
    fun log(priority: Priority, throwable: Throwable, message: () -> String) {
        performLog(priority, throwable = throwable, message = message)
    }

    @JvmStatic
    fun log(priority: Priority, throwable: Throwable) {
        performLog(priority, message = null, throwable = throwable)
    }

    @JvmStatic
    fun tag(explicit: String): KLogger {
        explicitTag.set(explicit)
        return this
    }

    @JvmStatic
    fun properties(values: Map<String, String>): KLogger {
        properties.set(values.toMap())
        return this
    }

    @JvmStatic
    fun properties(vararg values: Pair<String, String>): KLogger {
        properties.set(mapOf(*values))
        return this
    }

    private fun performLog(
        priority: Priority,
        throwable: Throwable?,
        message: (() -> String)?
    ) {
        val tag = explicitTag.get()
        explicitTag.set(null)

        val props = properties.get()
        properties.set(null)

        val messageString by lazy(mode = LazyThreadSafetyMode.NONE) {
            message?.invoke()
        }

        loggers.forEach {
            it.log(
                priority = priority,
                explicitTag = tag,
                inferredTag = inferTag(),
                throwable = throwable,
                message = messageString,
                properties = props
            )
        }
    }

    private fun inferTag(): String =
        Throwable().stackTrace
            .getOrElse(CALL_STACK_INDEX) { throw IllegalStateException("Synthetic stacktrace didn't have enough elements") }
            .let {
                val className =
                    ANONYMOUS_CLASS_PATTERN.matcher(it.className)
                        .takeIf(Matcher::find)
                        ?.replaceAll("")
                        ?: it.className

                "${className.substringAfterLast(".")}#${it.methodName}:${it.lineNumber}"
            }
}
