package ai.sterling.kchat.domain.initialization

interface AppInitializer {
    suspend fun initialize()
}
