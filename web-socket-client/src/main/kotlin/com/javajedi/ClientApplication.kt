package com.javajedi

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

suspend fun main() {
    val client = HttpClient {
        install(WebSockets)
    }
    coroutineScope {
        client.webSocket(method = HttpMethod.Get, host = "", port = 8080, path = "/chat") {
            val messageOutputRoutine = launch { outputMessages() }
            val messageInputRoutine = launch { inputMessages() }
            messageInputRoutine.join()
            messageOutputRoutine.join()
        }
    }
    client.close()
    println("Connection close. Goodbye!")
}

suspend fun DefaultClientWebSocketSession.outputMessages() {
    try {
        for (frame in incoming) {
            when (frame.frameType) {
                FrameType.TEXT -> {
                    val message = frame as Frame.Text
                    println(message.readText())

                }
                else -> println("Other types")
            }
        }
    } catch (ex: Exception) {
        println("Ex: ${ex.localizedMessage}")
    }
}

suspend fun DefaultClientWebSocketSession.inputMessages() {
    while (true) {
        val message = readLine() ?: ""
        if (message.equals("exit", true)) return
        try {
            send(message)
        } catch (ex: Exception) {
            println("Ex: ${ex.localizedMessage}")
        }
    }
}