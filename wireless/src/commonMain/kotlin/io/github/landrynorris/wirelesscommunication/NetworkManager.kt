package io.github.landrynorris.wirelesscommunication

import io.github.landrynorris.wirelesscommunication.NetworkState
import kotlinx.coroutines.flow.Flow

abstract class NetworkManager {
    abstract val networkState: Flow<NetworkState>
    abstract fun initialize()
}