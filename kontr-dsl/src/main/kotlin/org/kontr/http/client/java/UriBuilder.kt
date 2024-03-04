package org.kontr.http.client.java

import java.net.URI
import java.net.URLEncoder.encode
import java.nio.charset.StandardCharsets.UTF_8

class UriBuilder(private val baseUrl: String) {
    private val queryParams = mutableMapOf<String, String>()

    fun queryParam(key: String, value: String): UriBuilder {
        queryParams[key] = value
        return this
    }

    fun queryParams(entries: Map<String, String>): UriBuilder {
        queryParams.putAll(entries)
        return this
    }

    fun build(): URI {
        val uriBuilder = StringBuilder(baseUrl)

        if (queryParams.isNotEmpty()) {
            uriBuilder.append("?")
            queryParams.forEach { (key, value) ->
                uriBuilder.append(encode(key)).append("=").append(encode(value)).append("&")
            }
            uriBuilder.deleteCharAt(uriBuilder.length - 1) // Remove the last "&"
        }

        return URI.create(uriBuilder.toString())
    }

    private fun encode(key: String): String? = encode(key, UTF_8)
        .replace("+", "%20")
    // Temporary solution to the form encoded vs url encoded problem until a proper URL encoder is added
}
