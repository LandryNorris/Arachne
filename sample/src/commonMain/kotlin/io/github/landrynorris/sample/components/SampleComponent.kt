package io.github.landrynorris.sample.components

import com.arkivanov.decompose.ComponentContext
import io.github.landrynorris.wirelesscommunication.ConnectionManager
import io.github.landrynorris.wirelesscommunication.NetworkManager
import io.github.landrynorris.wirelesscommunication.WirelessDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

interface SampleLogic {
    val state: StateFlow<SampleState>

    fun init() {}
}

class SampleComponent(context: ComponentContext, private val manager: ConnectionManager):
    ComponentContext by context, SampleLogic {
    override val state = MutableStateFlow(SampleState())

    override fun init() {
        CoroutineScope(Dispatchers.Default).launch {
            manager.startAdvertising()

            manager.connections.collect { endpoints ->
                state.update { it.copy(devices = endpoints.map { endpoint ->
                    WirelessDevice(endpoint.id, endpoint.name)
                }) }
            }
        }
    }
}

data class SampleState(val isConnected: Boolean = false, val devices: List<WirelessDevice> = listOf())
