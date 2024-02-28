package org.kontr.generator.postman

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.kontr.dsl.HttpMethod

/**
 * @author Domingo Gomez
 */
@Serializable
data class PostmanCollection(
    val info: Map<String, String>,
    val item: List<Item>,
    val event: List<Event>? = mutableListOf(),
    val variable: List<Variable> = mutableListOf(),
)

@Serializable
data class Item(
    val name: String,
    val request: Request? = null,
    // val response: List<Map<String, String>>? = null, ignoring unknown keys
    val item: List<Item>? = null,
)

@Serializable
data class Request(
    val method: HttpMethod,
    val url: Url,
    val host: List<String> = mutableListOf(),
    val path: List<String> = mutableListOf(),
    /*val query: Map<String, String> = mutableMapOf(),*/
    val header: List<HeaderVariable> = mutableListOf(),
    val body: Body? = null,
)

@Serializable
data class Url(
    val raw: String,
    val host: List<String> = emptyList(),
    val path: List<String> = emptyList(),
    val query: List<QueryVariable> = emptyList(),
    val variable: List<Variable>? = mutableListOf(),
)

@Serializable
data class Body(
    val mode: String,
    val raw: String,
    val options: Map<String, Map<String, String>>? = mutableMapOf(),
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
    val value: String? = null,
    val type: String? = null,
    val description: String? = null,
)

@Serializable
data class QueryVariable(
    val key: String,
    val value: String? = null,
    val disabled: Boolean? = false,
)

@Serializable
data class HeaderVariable(
    val key: String,
    val value: String? = null,
    val disabled: Boolean? = false,
)