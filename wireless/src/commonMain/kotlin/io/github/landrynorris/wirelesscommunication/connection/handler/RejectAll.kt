package io.github.landrynorris.wirelesscommunication.connection.handler

import io.github.landrynorris.wirelesscommunication.Endpoint

class RejectAll: ConnectionFilter {
    override fun shouldAcceptConnection(endpoint: Endpoint): Boolean {
        return false
    }

    override fun shouldRequestConnection(endpoint: Endpoint): Boolean {
        return false
    }
}