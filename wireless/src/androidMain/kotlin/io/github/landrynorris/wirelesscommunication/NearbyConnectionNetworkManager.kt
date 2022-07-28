package io.github.landrynorris.wirelesscommunication

import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*

class NearbyConnectionNetworkManager(val context: Context) {
    suspend fun startAdvertising(name: String, serviceId: String) {
        val options = AdvertisingOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build()
        Nearby.getConnectionsClient(context).startAdvertising(name, serviceId, object: ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(p0: String, p1: ConnectionInfo) {
                println("Connection initiated")
            }

            override fun onConnectionResult(p0: String, p1: ConnectionResolution) {
                println("Got Connection Result")
            }

            override fun onDisconnected(p0: String) {
                println("Disconnected")
            }

        }, options).await()
    }

    suspend fun startDiscovery(serviceId: String) {
        val options = DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build()
        Nearby.getConnectionsClient(context).startDiscovery(serviceId, object: EndpointDiscoveryCallback() {
            override fun onEndpointFound(p0: String, p1: DiscoveredEndpointInfo) {
                println("Discovered endpoint")
            }

            override fun onEndpointLost(p0: String) {
                println("Lost endpoint")
            }
        }, options).await()
    }

    fun stopAdvertising() {
        Nearby.getConnectionsClient(context).stopAdvertising()
    }

    fun stopDiscovery() {
        Nearby.getConnectionsClient(context).stopDiscovery()
    }
}
