package io.github.landrynorris.sample.components

import com.arkivanov.decompose.ComponentContext
import io.github.landrynorris.wirelesscommunication.NetworkManager
import io.github.landrynorris.wirelesscommunication.WirelessDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface SampleLogic {
    val state: StateFlow<SampleState>

    fun init() {}
}

class SampleComponent(context: ComponentContext, private val manager: NetworkManager):
    ComponentContext by context, SampleLogic {
    override val state = MutableStateFlow(SampleState())

    override fun init() {
        manager.initialize()

        CoroutineScope(Dispatchers.Default).launch {
            manager.networkState.collectLatest { latestState ->
                state.update { it.copy(isConnected = latestState.isP2pEnabled, devices = latestState.devices) }
            }
        }
    }
}

data class SampleState(val isConnected: Boolean = false, val devices: List<WirelessDevice> = listOf())
