package org.kontr.generator.openapi

import kotlinx.serialization.json.Json
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
            variables = if (it.value.parameters.isEmpty()) mutableMapOf()
            else it.value.parameters.filter { p -> p.`in` == In.PATH }.associate { param -> param.name to "" },
            path = path.key.split("/").filter { segment -> segment.isNotEmpty() },
            query = if (it.value.parameters.isEmpty()) mutableMapOf()
            else it.value.parameters.filter { p -> p.`in` == In.QUERY }.associate { param -> param.name to "" },
        )
        return@map Item(
            name = it.value.operationId ?: path.key.toClassName(),
            request = Request(method = mapHttpMethod(it.key), url = url, header = emptyMap(), body = ""),
            items = emptyList()
        )
    }

    private fun mapHttpMethod(key: String): HttpMethod = HttpMethod.valueOf(key.uppercase())
}