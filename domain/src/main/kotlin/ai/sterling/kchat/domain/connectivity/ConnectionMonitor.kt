package ai.sterling.kchat.domain.connectivity

import kotlinx.coroutines.flow.Flow

interface ConnectionMonitor {
    suspend fun getConnectionState(): Flow<ConnectionState>
}
