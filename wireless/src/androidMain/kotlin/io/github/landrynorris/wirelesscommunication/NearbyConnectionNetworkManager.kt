package io.github.landrynorris.wirelesscommunication

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import com.google.android.gms.nearby.connection.Payload as AndroidPayload

class NearbyConnectionNetworkManager(val context: Context,
                                     private val sessionInfo: SessionInfo): ConnectionManager() {

    /**
     * A flow that emits a value when a new payload is received.
     */
    override val payloadFlow = MutableStateFlow(Payload(byteArrayOf()))

    /**
     * List of active endpoints
     */
    override val connections = MutableStateFlow(listOf<Endpoint>())

    override suspend fun startAdvertising() {
        val options = AdvertisingOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build()
        Nearby.getConnectionsClient(context).startAdvertising(sessionInfo.device,
            sessionInfo.sessionName, object: ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
                Nearby.getConnectionsClient(context).acceptConnection(endpointId, object: PayloadCallback() {
                    override fun onPayloadReceived(endpointId: String, payload: AndroidPayload) {
                        payloadFlow.update { Payload(payload.asBytes() ?: byteArrayOf()) }
                    }

                    override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
                        if(update.status == PayloadTransferUpdate.Status.SUCCESS) println("Transfer success")
                    }
                })
            }

            override fun onConnectionResult(p0: String, p1: ConnectionResolution) {
                println("Got Connection Result")
            }

            override fun onDisconnected(p0: String) {
                println("Disconnected")
            }

        }, options).await()
    }

    override suspend fun startDiscovery() {
        val options = DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build()
        Nearby.getConnectionsClient(context).startDiscovery(sessionInfo.sessionName,
            object: EndpointDiscoveryCallback() {
            override fun onEndpointFound(id: String, endpointInfo: DiscoveredEndpointInfo) {
                val newEndpoint = Endpoint(id, endpointInfo.endpointName)
                connections.update { it + newEndpoint }
            }

            override fun onEndpointLost(id: String) {
                connections.update { it.filter { endpoint -> endpoint.id != id } }
            }
        }, options).await()
    }

    fun stopAdvertising() {
        Nearby.getConnectionsClient(context).stopAdvertising()
    }

    fun stopDiscovery() {
        Nearby.getConnectionsClient(context).stopDiscovery()
    }

    fun sendPayload(endpoint: String, data: ByteArray) {
        Nearby.getConnectionsClient(context).sendPayload(endpoint, AndroidPayload.fromBytes(data))
    }
}
