package ai.sterling.logger

interface Logger {

    /**
     * [priority] Defines importance of logged event
     * [explicitTag] Custom value provided by user at [SpireLogger.tag]
     * [inferredTag] Automatically generated value providing information from where logging event originated
     * [message] Optional message passed to logged event
     * [throwable] Optional error information attached to logged event
     */
    fun log(
        priority: Priority,
        explicitTag: String?,
        inferredTag: String,
        message: String?,
        throwable: Throwable?,
        properties: Map<String, String>?
    )

    enum class Priority {
        VERBOSE,
        DEBUG,
        INFO,
        WARNING,
        ERROR,
        WTF
    }
}
