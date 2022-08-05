package io.github.landrynorris.wirelesscommunication

import kotlinx.serialization.ExperimentalSerializationApi
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

    fun <T> send(endpoint: Endpoint, value: T, serializer: SerializationStrategy<T>) {
        val data = protobuf.encodeToByteArray(serializer, value)
        driver.sendPayload(endpoint, payload = Payload(data))
    }

    fun listenForPayloads() = driver.payloadFlow
}
