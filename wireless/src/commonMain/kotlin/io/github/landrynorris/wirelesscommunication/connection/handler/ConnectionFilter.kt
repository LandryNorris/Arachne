package io.github.landrynorris.wirelesscommunication.connection.handler

import io.github.landrynorris.wirelesscommunication.Endpoint

interface ConnectionFilter {
    fun shouldAcceptConnection(endpoint: Endpoint): Boolean
    fun shouldRequestConnection(endpoint: Endpoint): Boolean
}