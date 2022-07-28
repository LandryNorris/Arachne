package io.github.landrynorris.wirelesscommunication

data class WirelessDevice(val address: String, val name: String) {
    override fun toString(): String {
        return "$name@$address"
    }
}
