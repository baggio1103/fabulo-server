package com.javajedi.plugins

import com.javajedi.Connection
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration
import java.util.*


/**
 *  To cancel connection:
 *  send(Frame.Close(CloseReason(CloseReason.Codes.NORMAL, "you are shit")))
 *
 *  To read image from file and send image
 *   outgoing.send(Frame.Binary(true, withContext(Dispatchers.IO) {
 *   Files.readAllBytes(File("kotlin.png").toPath())
 *   }))

 * */


fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        val connections = Collections.synchronizedSet<Connection>(LinkedHashSet())
        webSocket("/chat") { // websocketSession
            println("Connecting a new user")
            val thisConnection = Connection(this)
            connections += thisConnection
            try {
                send("You connected! There are ${connections.size} connections")
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val receivedText = frame.readText()
                    val textWithUserName = "[${thisConnection.name}]: $receivedText"
                    connections.forEach {
                        it.session.send(textWithUserName)
                    }
                }
            } catch (ex: Exception) {
                println("Exception: ${ex.localizedMessage}")
            } finally {
                println("Removing this connection $thisConnection")
                connections -= thisConnection
            }
        }

    }
}
