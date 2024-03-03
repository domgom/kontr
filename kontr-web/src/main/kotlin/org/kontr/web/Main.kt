package org.kontr.web

import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.doublereceive.*
import org.kontr.web.plugins.configureRouting

/**
 * @author Domingo Gomez
 */
fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    install(DoubleReceive)
    install(CORS) {
        anyHost()
    }
    configureRouting()
}