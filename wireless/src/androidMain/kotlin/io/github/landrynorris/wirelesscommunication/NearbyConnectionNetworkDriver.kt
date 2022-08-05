package io.github.landrynorris.wirelesscommunication

import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import com.google.android.gms.nearby.connection.Payload as AndroidPayload

class NearbyConnectionNetworkDriver(val context: Context,
                                    private val sessionInfo: SessionInfo): ConnectionDriver() {

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
                        println("Received Payload. Sending to flow")
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
            override fun onEndpointFound(id: String, info: DiscoveredEndpointInfo) {
                val newEndpoint = Endpoint(id, info.endpointName)
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

    override fun sendPayload(endpoint: Endpoint, payload: Payload) {
        Nearby.getConnectionsClient(context).sendPayload(endpoint.id, AndroidPayload.fromBytes(payload.bytes))
    }
}
