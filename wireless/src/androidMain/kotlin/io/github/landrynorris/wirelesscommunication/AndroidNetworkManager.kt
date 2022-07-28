package io.github.landrynorris.wirelesscommunication

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.UpnpServiceResponseListener
import android.net.wifi.p2p.nsd.WifiP2pServiceInfo
import android.net.wifi.p2p.nsd.WifiP2pServiceRequest
import android.net.wifi.p2p.nsd.WifiP2pUpnpServiceInfo
import android.net.wifi.p2p.nsd.WifiP2pUpnpServiceRequest
import kotlinx.coroutines.flow.MutableStateFlow

class AndroidNetworkManager(val activity: Activity, val sessionInfo: SessionInfo): NetworkManager() {
    override val networkState = MutableStateFlow(NetworkState())

    private val intentFilter = IntentFilter().apply {
        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }

    private val manager: WifiP2pManager by lazy(LazyThreadSafetyMode.NONE) {
        activity.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    }

    private lateinit var receiver: WirelessBroadcastReceiver
    private lateinit var channel: WifiP2pManager.Channel

    @SuppressLint("MissingPermission")
    override fun initialize() {
        println("Initializing network manager")
        channel = manager.initialize(activity, activity.mainLooper, null)

        val info = WifiP2pUpnpServiceInfo.newInstance(sessionInfo.uuid, sessionInfo.device, mutableListOf())
        manager.addLocalService(channel, info, p2pErrorListener)

        manager.setUpnpServiceResponseListener(channel) { config, pnpDevice ->
            val device = WirelessDevice(pnpDevice.deviceAddress, pnpDevice.deviceName)
            println("Configuration is ${config.joinToString(", ")}")
            println("Device is $device")
        }

        manager.addServiceRequest(channel, WifiP2pUpnpServiceRequest.newInstance(), p2pErrorListener)

        manager.discoverServices(channel, p2pErrorListener)
        //resume()
    }

    fun resume() {
        activity.registerReceiver(receiver, intentFilter)
    }

    fun pause() {
        activity.unregisterReceiver(receiver)
    }

    private val emptyActionListener = object: WifiP2pManager.ActionListener {
        override fun onSuccess() = println("success")
        override fun onFailure(code: Int) = println("failure: $code")
    }

    private val p2pErrorListener = object : WifiP2pManager.ActionListener {
        override fun onSuccess() = println("success")
        override fun onFailure(code: Int) = println("Error is ${
            when(code) {
                WifiP2pManager.P2P_UNSUPPORTED -> "Unsupported"
                WifiP2pManager.ERROR -> "Generic error"
                WifiP2pManager.BUSY -> "Busy"
                else -> "Error not defined"
            }
        }")
    }
}
