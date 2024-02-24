package org.kontr.web.plugins

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.div
import kotlinx.html.stream.appendHTML
import org.kontr.generator.postman.PostmanGenerator
import org.kontr.web.views.boxed
import org.kontr.web.views.index
import java.io.File

fun Application.configureRouting() {
    routing {
        staticResources("/", "static", index = "index.html") {
            preCompressed(CompressedFileType.BROTLI, CompressedFileType.GZIP)
        }

        route("/upload") {
            post("") {
                var fileDescription = ""
                var fileName = ""
                val filePath = "/tmp"
                val multipartData = call.receiveMultipart()
                multipartData.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            fileDescription = part.value
                        }

                        is PartData.FileItem -> {
                            fileName = part.originalFileName as String
                            val fileBytes = part.streamProvider().readBytes()

                            File("$filePath/$fileName").writeBytes(fileBytes)
                        }

                        else -> {}
                    }
                    part.dispose()
                }
                val generatedCollection = PostmanGenerator().generate(
                    "$filePath/$fileName",
                    "org.example.company",
                    fileName + "_generated"
                )
                val stringBuilder = StringBuilder()
                generatedCollection.writeTo(stringBuilder)
                val byteArray = stringBuilder.toString().toByteArray()
                call.respondBytes(byteArray, ContentType.Text.Plain)
            }
        }
    }
}





