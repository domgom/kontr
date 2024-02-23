package org.kontr.generator.postman

import kotlinx.serialization.Serializable
import org.kontr.dsl.HttpMethod

/**
 * @author Domingo Gomez
 */
@Serializable
data class PostmanCollection(
    val info: Map<String, String>,
    val item: List<CollectionItem>,
    val event: List<Event>? = mutableListOf(),
    val variable: List<Variable> = mutableListOf(),
)

@Serializable
data class CollectionItem(
    val name: String,
    val request: Request? = null,
    val response: List<Map<String, String>>? = null,
    val item: List<CollectionItem>? = null,
)

@Serializable
data class Request(
    val method: HttpMethod,
    val url: Url,
    val host: List<String> = mutableListOf(),
    val path: List<String> = mutableListOf(),
    /*val query: Map<String, String> = mutableMapOf(),*/
    val header: List<Map<String, String>> = mutableListOf(),
    val body: Body? = null,
)

@Serializable
data class Url(
    val raw: String,
    val host: List<String> = emptyList(),
    val path: List<String> = emptyList(),
    val query: List<Map<String, String>> = emptyList(),
    val variable: List<Variable>? = mutableListOf(),
)

@Serializable
data class Body(
    val mode: String,
    val raw: String,
    val options: Map<String, Map<String, String>>,
)

@Serializable
data class Event(
    val listen: String,
    val script: Script? = null,
)

@Serializable
data class Script(
    val type: String,
    val exec: List<String> = mutableListOf(),
)

@Serializable
data class Variable(
    val key: String,
    val value: String,
    val type: String? = null,
)