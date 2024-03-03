package org.kontr.web.plugins

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
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
                var nestedObjects: Boolean = false
                var addRunCollection: Boolean = true
                var addEnv: Boolean = true
                var envName: String = "Env"
                var packageName: String = "org.example.generated"
                var fileName: String = "Collection"

                call.receiveMultipart().forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            when (part.name) {
                                "nestedObjects" -> nestedObjects = part.value == "on"
                                "addRunCollection" -> addRunCollection = part.value == "on"
                                "addEnv" -> addEnv = part.value == "on"
                                "envName" -> envName = part.value
                                "packageName" -> packageName = part.value
                                "fileName" -> fileName = part.value
                            }
                            println(part.name + ":" + part.value)
                        }

                        is PartData.FileItem -> {
                            val generatedCollection = generatorFacade.generateFromStreamToStream(
                                part.streamProvider().buffered(),
                                generationOptions = GenerationOptions(
                                    nestedObjects = nestedObjects,
                                    addRunCollection = addRunCollection,
                                    addEnv = addEnv,
                                    envName = envName,
                                    packageName = packageName,
                                    fileName = fileName
                                )
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





