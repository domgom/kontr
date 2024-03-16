package org.kontr.generator.openapi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @author Domingo Gomez
 */
@Serializable
data class OpenApiCollection(
    val openapi: String,
    val info: Info? = null,
    val paths: Map<String, Map<String, Operation>>
)

@Serializable
data class Info(
    val title: String,
    val version: String
)
/*@Serializable
data class PathItem(
    val Map<String, Operation> = emptyMap()
    *//*val get: Operation? = null,
    val post: Operation? = null,
    val put: Operation? = null,
    val delete: Operation? = null,
    val patch: Operation? = null,
    val head: Operation? = null,
    val options: Operation? = null,
    val trace: Operation? = null,
    val connect: Operation? = null*//*
) {
    fun getAllMethods(): List<Operation> = listOfNotNull(get, post, put, delete, patch, head, options, trace, connect)
}*/

@Serializable
data class Operation(
    val tags: List<String>? = null,
    val operationId: String? = null,
    val parameters: List<Parameter> = emptyList(),
    val responses: Map<String, ApiResponse>? = null
)

@Serializable
data class Parameter(
    val name: String,
    val `in`: In,
    val required: Boolean,
    val schema: ParameterSchema,
)

@Serializable
enum class In {
    @SerialName("path")
    PATH,

    @SerialName("query")
    QUERY,

    @SerialName("header")
    HEADER,

    @SerialName("cookie")
    COOKIE,
}

@Serializable
data class ParameterSchema(
    // Should have type or ref
    val type: String? = null,
    val format: String? = null,
    val ref: String? = null,
)

@Serializable
data class ApiResponse(
    val description: String? = null,
    val content: Map<String, MediaType>? = null
)

@Serializable
data class MediaType(
    val schema: SchemaReference? = null
)

@Serializable
data class SchemaReference(
    val `$ref`: String? = null
)