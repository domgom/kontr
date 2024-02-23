package org.kontr.web

import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.kontr.web.plugins.configureRouting

/**
 * @author Domingo Gomez
 */
fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    configureRouting()
}