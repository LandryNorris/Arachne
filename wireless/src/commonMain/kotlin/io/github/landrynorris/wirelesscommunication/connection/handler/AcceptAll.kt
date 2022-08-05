package io.github.landrynorris.wirelesscommunication.connection.handler

import io.github.landrynorris.wirelesscommunication.Endpoint

class AcceptAll: ConnectionFilter {
    override fun shouldAcceptConnection(endpoint: Endpoint): Boolean {
        return true
    }

    override fun shouldRequestConnection(endpoint: Endpoint): Boolean {
        return true
    }
}