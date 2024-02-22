package org.kontr.generator.postman

import kotlinx.serialization.Serializable
import org.kontr.dsl.HttpMethod

/**
 * @author Domingo Gomez
 */
@Serializable
data class PostmanCollection(val info: Map<String, String>, val items: List<CollectionItem>)

@Serializable
data class CollectionItem(val name: String, val request: Request?, val item: List<CollectionItem>?)

@Serializable
data class Request(
    val method: HttpMethod,
    val url: String,
    val host: List<String>,
    val path: List<String>,
    val query: Map<String, String> = emptyMap(),
    val headers: Map<String, String>,
    val variables: Map<String, String>,
    val body: String?
)