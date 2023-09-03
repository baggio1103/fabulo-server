package com.javajedi

import com.javajedi.plugins.configureRouting
import com.javajedi.plugins.configureSockets
import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain.main

fun main(args: Array<String>) {
    main(args)
}

fun Application.module() {
    configureSockets()
    configureRouting()
}
