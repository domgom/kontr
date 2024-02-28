package org.kontr.generator.core

import org.kontr.dsl.HttpMethod

/**
 * @author Domingo Gomez
 */
data class GeneratorCollection(
    val name: String,
    val items: List<Item> = mutableListOf(),
    val variables: Map<String, String> = mutableMapOf(),
)

data class Item(
    val name: String,
    val request: Request? = null,
    val items: List<Item>? = null,
) {
    fun isRequest() = request != null
    fun isFolder() = items != null
}

data class Request(
    val method: HttpMethod,
    val url: Url,
    val path: List<String> = mutableListOf(),
    val header: Map<String, String> = mutableMapOf(),
    val body: String? = null,
)

data class Url(
    val raw: String,
    val host: String,
    val path: List<String>,
    val query: Map<String, String> = mapOf(),
    val variables: Map<String, String> = mapOf(),
)
