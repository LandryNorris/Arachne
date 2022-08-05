package io.github.landrynorris.wirelesscommunication

import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import io.github.landrynorris.wirelesscommunication.connection.handler.ConnectionFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import com.google.android.gms.nearby.connection.Payload as AndroidPayload

class NearbyConnectionNetworkDriver(val context: Context,
                                    private val sessionInfo: SessionInfo,
                                    private val handler: ConnectionFilter): ConnectionDriver() {

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
            override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) = Unit

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
                val endpoint = Endpoint(id, info.endpointName)
                if(handler.shouldRequestConnection(endpoint)) {
                    connect(endpoint)
                }
            }

            override fun onEndpointLost(id: String) = Unit
        }, options).await()
    }

    fun connect(endpoint: Endpoint) {
        Nearby.getConnectionsClient(context).requestConnection(sessionInfo.device, endpoint.id,
            object: ConnectionLifecycleCallback() {
                override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
                    val endpoint = Endpoint(endpointId, info.endpointName)
                    if(handler.shouldAcceptConnection(endpoint)) {
                        accept(endpoint)
                    }
                }

                override fun onConnectionResult(endpointId: String, info: ConnectionResolution) {
                    connections.update { it + Endpoint(endpointId, "") }
                }

                override fun onDisconnected(endpointId: String) {
                    connections.update { it.filter { endpoint -> endpoint.id != endpointId } }
                }
            })
    }

    fun accept(endpoint: Endpoint) {
        Nearby.getConnectionsClient(context).acceptConnection(endpoint.id, object: PayloadCallback() {
            override fun onPayloadReceived(endpointId: String, payload: AndroidPayload) {
                println("Received Payload. Sending to flow")
                payloadFlow.update { Payload(payload.asBytes() ?: byteArrayOf()) }
            }

            override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
                if(update.status == PayloadTransferUpdate.Status.SUCCESS) println("Transfer success")
                else if(update.status == PayloadTransferUpdate.Status.FAILURE) println("Transfer failed")
            }
        })
    }

    override fun stopAdvertising() {
        Nearby.getConnectionsClient(context).stopAdvertising()
    }

    override fun stopDiscovery() {
        Nearby.getConnectionsClient(context).stopDiscovery()
    }

    override fun sendPayload(endpoint: Endpoint, payload: Payload) {
        Nearby.getConnectionsClient(context).sendPayload(endpoint.id, AndroidPayload.fromBytes(payload.bytes))
    }
}
