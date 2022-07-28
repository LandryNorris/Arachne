package io.github.landrynorris.wirelesscommunication

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}