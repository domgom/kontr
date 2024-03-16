package org.kontr.generator.openapi

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.kontr.dsl.HttpMethod
import org.kontr.generator.core.GeneratorCollection
import org.kontr.generator.core.IParser
import org.kontr.generator.core.Item
import org.kontr.generator.core.Request
import java.io.InputStream
import java.io.InputStreamReader

/**
 * @author Domingo Gomez
 */
class OpenApiParser : IParser {
    private val json = Json { ignoreUnknownKeys = true }

    override fun parseGeneratorCollection(collection: InputStream): GeneratorCollection {
        val openApiCollection = parseOpenApiCollection(collection)
        val generatorCollection = mapCollection(openApiCollection)
        return generatorCollection
    }

    private fun parseOpenApiCollection(collection: InputStream): OpenApiCollection {
        val openApiCollection = json.decodeFromString<OpenApiCollection>(InputStreamReader(collection).readText())
        return openApiCollection
    }

    //TODO move to utils
    private fun String.toClassName(): String {
        // Ensure the class name is a valid Kotlin identifier
        return this.replace(Regex("[^A-Za-z0-9_]"), "").capitalize()
    }

    private fun mapCollection(openApiCollection: OpenApiCollection): GeneratorCollection {
        val generatorCollection = GeneratorCollection(
            name = openApiCollection.info?.title?.toClassName() ?: "collection",
            items = openApiCollection.paths.map { mapPaths(it) }.flatten(),
            variables = emptyMap() // TODO
        )
        return generatorCollection
    }

    private fun mapPaths(path: Map.Entry<String, Map<String, Operation>>): List<Item> = path.value.map {
        val url = org.kontr.generator.core.Url(
            raw = path.key,
            host = path.key, // TODO pass server host
            path = path.key.split("/").filter { segment -> segment.isNotEmpty() },
            variables = paramInToMap(In.PATH, it.value.parameters),
            query = paramInToMap(In.QUERY, it.value.parameters),
        )
        return@map Item(
            name = it.value.operationId ?: path.key.toClassName(),
            items = emptyList(),
            request = Request(
                method = mapHttpMethod(it.key), url = url, body = "",
                header = paramInToMap(In.HEADER, it.value.parameters),
            ),
        )
    }

    private fun paramInToMap(location: In, parameters: List<Parameter>): Map<String, String> =
        if (parameters.isEmpty()) mutableMapOf()
        else parameters.filter { p -> p.`in` == location }.associate { param -> param.name to toDefaultValue(param) }

    private fun toDefaultValue(param: Parameter): String {
        return if (param.schema.default != null) {
            when (param.schema.type) {
                "array" -> param.schema.default.jsonArray.joinToString(",")
                "string", "number", "integer", "boolean" -> param.schema.default.jsonPrimitive.content
                "object" -> param.schema.default.jsonObject.toString()
                else -> param.schema.default.toString()
            }
        } else {
            ""
        }
    }

    private fun mapHttpMethod(key: String): HttpMethod = HttpMethod.valueOf(key.uppercase())
}