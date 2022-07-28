package io.github.landrynorris.wirelesscommunication

data class NetworkState(val isP2pEnabled: Boolean = false, val devices: List<WirelessDevice> = listOf())