package org.kontr.web.plugins

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import org.kontr.generator.core.GenerationOptions
import org.kontr.generator.core.GeneratorFacade
import org.kontr.generator.postman.PostmanParser

val generatorFacade = GeneratorFacade(parser = PostmanParser())
fun Application.configureRouting() {
    routing {
        staticResources("/", "static", index = "index.html") {
            preCompressed(CompressedFileType.BROTLI, CompressedFileType.GZIP)
        }

        route("/upload") {
            post("") {
                val form = call.receiveParameters().toMap()
                call.receiveMultipart().forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> {
                            val generatedCollection = generatorFacade.generateFromStreamToStream(
                                part.streamProvider().buffered(),
                                GenerationOptions(form.mapValues { it.value.joinToString(",") })
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





