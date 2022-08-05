package io.github.landrynorris.sample.components

import com.arkivanov.decompose.ComponentContext
import io.github.landrynorris.wirelesscommunication.ConnectionDriver
import io.github.landrynorris.wirelesscommunication.WirelessDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface DeviceListLogic {
    val state: StateFlow<DeviceListState>

    fun init() {}
}

class DeviceListComponent(context: ComponentContext, private val manager: ConnectionDriver):
    ComponentContext by context, DeviceListLogic {
    override val state = MutableStateFlow(DeviceListState())

    override fun init() {
        CoroutineScope(Dispatchers.Default).launch {
            manager.startAdvertising()
            manager.startDiscovery()

            manager.connections.collect { endpoints ->
                state.update { it.copy(devices = endpoints.map { endpoint ->
                    WirelessDevice(endpoint.id, endpoint.name)
                }) }
            }
        }
    }
}

data class DeviceListState(val isConnected: Boolean = false, val devices: List<WirelessDevice> = listOf())
