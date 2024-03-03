package org.kontr.web.plugins

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
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
                var nestedObjects: Boolean
                var addRunCollection:Boolean
                var addEnv:Boolean
                var envName:String
                var packageName: String
                var fileName: String

                call.receiveMultipart().forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> {

                            val generatedCollection = generatorFacade.generateFromStreamToStream(
                                part.streamProvider().buffered(),
                                "org.example.company",
                                "Collection"
                            )
                            val stringBuilder = StringBuilder()
                            generatedCollection.writeTo(stringBuilder)
                            val byteArray = stringBuilder.toString().toByteArray()
                            call.respondBytes(byteArray, ContentType.Text.Plain)
                        }
                        is PartData.FormItem ->{
                            when(part.name){
                                "nestedObjects" -> nestedObjects = part.value == "on"
                                "addRunCollection" -> addRunCollection = part.value == "on"
                                "addEnv" -> addEnv = part.value == "on"
                                "envName" -> envName = part.value
                                "packageName" -> packageName = part.value
                                "fileName" -> fileName = part.value
                            }
                            println(part.name + ":" +part.value)
                        }
                        else -> {}
                    }
                    part.dispose()
                }
            }


        }
    }
}





