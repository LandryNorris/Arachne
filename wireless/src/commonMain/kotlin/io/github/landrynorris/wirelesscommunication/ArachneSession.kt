package io.github.landrynorris.wirelesscommunication

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.protobuf.ProtoBuf

@OptIn(ExperimentalSerializationApi::class)
class ArachneSession(private val driver: ConnectionDriver) {
    private val protobuf = ProtoBuf {
        encodeDefaults = true
    }

    suspend fun initialize() {
        driver.startAdvertising()
        driver.startDiscovery()
    }

    fun destroy() {
        driver
    }

    fun <T> send(endpoint: Endpoint, value: T, strategy: SerializationStrategy<T>) {
        val data = protobuf.encodeToByteArray(strategy, value)
        driver.sendPayload(endpoint, payload = Payload(data))
    }

    private fun listenForPayloads() = driver.payloadFlow

    fun <T> listen(strategy: DeserializationStrategy<T>): Flow<T> = listenForPayloads().mapNotNull {
        try {
            protobuf.decodeFromByteArray(strategy, it.bytes)
        } catch(e: SerializationException) {
            e.printStackTrace()
            null
        }
    }
}
