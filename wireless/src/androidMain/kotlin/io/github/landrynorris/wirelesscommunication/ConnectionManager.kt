package io.github.landrynorris.wirelesscommunication

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

fun createOrJoin() {

}



class WirelessBroadcastReceiver(val manager: WifiP2pManager,
                                val channel: WifiP2pManager.Channel,
                                val networkState: MutableStateFlow<NetworkState> =
                                    MutableStateFlow(NetworkState())): BroadcastReceiver() {

    @SuppressLint("MissingPermission") //we should check for permissions elsewhere
    override fun onReceive(context: Context, i: Intent) {
        println("Got WiFi status message with action ${i.action}")
        when(i.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                println("P2P State Changed")
                val state = i.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                networkState.update {
                    it.copy(isP2pEnabled = state == WifiP2pManager.WIFI_P2P_STATE_ENABLED)
                }
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                println("Peers Changed")
                manager.requestPeers(channel) { response ->
                    networkState.update {
                        it.copy(devices = response.deviceList.map { device ->
                            WirelessDevice(device.deviceAddress, device.deviceName)
                        })
                    }
                }
            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                val p2pDevice = i.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE) as WifiP2pDevice? ?: return
                val device = WirelessDevice(p2pDevice.deviceAddress, p2pDevice.deviceName)
                println("This device is $device")
            }
        }
    }
}
