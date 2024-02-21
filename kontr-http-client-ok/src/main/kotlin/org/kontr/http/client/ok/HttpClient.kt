package org.kontr.http.client.ok

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.kontr.dsl.RequestDsl
import org.kontr.dsl.ResponseDsl
import org.kontr.dsl.SyncHttpClient

/**
 * @author Domingo Gomez
 */
class SyncOkHttpClient : SyncHttpClient {
    private val client = OkHttpClient()

    override fun execute(request: RequestDsl): ResponseDsl = client.newCall(request.map()).execute().map()

    fun RequestDsl.map(): okhttp3.Request = okhttp3.Request.Builder()
        .url(url)
        .method(
            method = method.name,
            body = body?.toRequestBody("application/json".toMediaType()) //TODO add media type in Request
        )
        .headers(headers.map())
        .build()
        .also { println(this) }

    private fun okhttp3.Response.map(): ResponseDsl = ResponseDsl().apply {
        body = this@map.body?.string() ?: ""
        headers = this@map.headers.toMultimap()
        statusCode = code
    }.apply { println(this) }

    private fun MutableMap<String, List<String>>.map(): okhttp3.Headers {
        val builder = okhttp3.Headers.Builder()
        this.forEach { builder[it.key] = it.value.joinToString { "," } }
        return builder.build()
    }
}