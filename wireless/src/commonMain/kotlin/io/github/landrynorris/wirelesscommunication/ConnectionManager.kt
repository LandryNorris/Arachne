package io.github.landrynorris.wirelesscommunication

import kotlinx.coroutines.flow.Flow

abstract class ConnectionManager {
    abstract val payloadFlow: Flow<Payload>
    abstract val connections: Flow<List<Endpoint>>

    abstract suspend fun startAdvertising()
    abstract suspend fun startDiscovery()

}