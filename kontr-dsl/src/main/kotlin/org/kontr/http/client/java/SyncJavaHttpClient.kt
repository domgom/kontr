package org.kontr.http.client.java

import org.kontr.dsl.RequestDsl
import org.kontr.dsl.ResponseDsl
import org.kontr.dsl.SyncHttpClient
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse

/**
 * @author Domingo Gomez
 */
class SyncJavaHttpClient : SyncHttpClient {
    private val client = HttpClient.newHttpClient()

    override fun execute(request: RequestDsl): ResponseDsl =
        client.send(request.map(), HttpResponse.BodyHandlers.ofString()).map()

    private fun RequestDsl.map(): HttpRequest {
        val builder = HttpRequest.newBuilder()
            .uri(UriBuilder(url).queryParams(queryParams).build())
            .method(method.name, body?.let { BodyPublishers.ofString(it) } ?: BodyPublishers.noBody())

        headers.entries.forEach { entry ->
            entry.value.forEach {
                builder.header(entry.key, it)
            }
        }

        return builder.build()
    }

    private fun <T> HttpResponse<T>.map(): ResponseDsl = ResponseDsl().apply {
        statusCode = statusCode()
        body = body() as String //FIXME add typed responses support
        headers = headers().map()
    }
}

