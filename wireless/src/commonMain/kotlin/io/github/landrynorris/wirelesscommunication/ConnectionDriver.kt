package io.github.landrynorris.wirelesscommunication

import kotlinx.coroutines.flow.Flow

abstract class ConnectionDriver {
    abstract val payloadFlow: Flow<Payload>
    abstract val connections: Flow<List<Endpoint>>

    abstract suspend fun startAdvertising()
    abstract suspend fun startDiscovery()

    abstract fun sendPayload(endpoint: Endpoint, payload: Payload)

    abstract fun stopDiscovery()
    abstract fun stopAdvertising()
}