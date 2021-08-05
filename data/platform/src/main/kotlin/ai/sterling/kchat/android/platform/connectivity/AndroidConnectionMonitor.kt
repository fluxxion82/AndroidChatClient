package ai.sterling.kchat.android.platform.connectivity

import ai.sterling.kchat.domain.base.CoroutineScopeFacade
import ai.sterling.kchat.domain.connectivity.persistence.ConnectionMonitor
import ai.sterling.kchat.domain.connectivity.model.ConnectionState
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class AndroidConnectionMonitor @Inject constructor(
    @ApplicationContext context: Context,
    private val contextFacade: CoroutineScopeFacade
) : ConnectionMonitor {

    private val connectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val connectionState = BroadcastChannel<ConnectionState>(1)

    override suspend fun getConnectionState(): Flow<ConnectionState> {
        val channel = connectionState.openSubscription()
        updateConnectionState(isConnected())

        return channel.consumeAsFlow()
            .onStart { registerCallback() }
            .onCompletion { unregisterCallback() }
    }

    private suspend fun updateConnectionState(state: ConnectionState) {
        connectionState.send(state)
    }

    private fun isConnected(): ConnectionState {
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo

        return if (activeNetwork?.isConnected == true) {
            ConnectionState.CONNECTED
        } else {
            ConnectionState.DISCONNECTED
        }
    }

    private fun registerCallback() {
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    private fun unregisterCallback() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            contextFacade.globalScope.launch {
                updateConnectionState(ConnectionState.CONNECTED)
            }
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            contextFacade.globalScope.launch {
                updateConnectionState(ConnectionState.DISCONNECTED)
            }
        }
    }
}
