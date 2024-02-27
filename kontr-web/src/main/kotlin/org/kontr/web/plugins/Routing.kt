package org.kontr.web.plugins

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kontr.generator.postman.PostmanGenerator

fun Application.configureRouting() {
    routing {
        staticResources("/", "static", index = "index.html") {
            preCompressed(CompressedFileType.BROTLI, CompressedFileType.GZIP)
        }

        route("/upload") {
            post("") {
                call.receiveMultipart().forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> {
                            val generatedCollection = PostmanGenerator().generateFromStreamToStream(
                                part.streamProvider().buffered(),
                                "org.example.company",
                                "Collection"
                            )
                            val stringBuilder = StringBuilder()
                            generatedCollection.writeTo(stringBuilder)
                            val byteArray = stringBuilder.toString().toByteArray()
                            call.respondBytes(byteArray, ContentType.Text.Plain)
                        }

                        else -> {}
                    }
                    part.dispose()
                }
            }
        }
    }
}





