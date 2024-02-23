package org.kontr.web.plugins

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.*

fun Application.configureRouting() {
    routing {
        route("kontr") {
            get("/") {
                call.respondHtml {
                    body {
                        div {
                            ol {
                                for (i in 0..3) {
                                    li {
                                        span {
                                            +"Number:$i"
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}