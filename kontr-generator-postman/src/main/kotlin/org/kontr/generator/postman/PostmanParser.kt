package org.kontr.generator.postman

import kotlinx.serialization.json.Json
import org.kontr.generator.core.GeneratorCollection
import org.kontr.generator.core.IParser
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader

/**
 * @author Domingo Gomez
 */
class PostmanParser : IParser {
    private val json = Json { ignoreUnknownKeys = true }

    fun parseGeneratorCollection(inputPath: String): GeneratorCollection =
        parseGeneratorCollection(File(inputPath).inputStream())

    override fun parseGeneratorCollection(collection: InputStream): GeneratorCollection {
        val postmanCollection = parsePostmanCollection(collection)
        val generatorCollection = mapCollection(postmanCollection)
        return generatorCollection
    }

    private fun parsePostmanCollection(collection: InputStream): PostmanCollection {
        val postmanCollection = json.decodeFromString<PostmanCollection>(InputStreamReader(collection).readText())
        return PostmanCollection(postmanCollection.info, postmanCollection.item)
    }

    //TODO move to utils
    private fun String.toClassName(): String {
        // Ensure the class name is a valid Kotlin identifier
        return this.replace(Regex("[^A-Za-z0-9_]"), "").capitalize()
    }

    private fun mapCollection(postmanCollection: PostmanCollection): GeneratorCollection {
        val generatorCollection = GeneratorCollection(
            name = postmanCollection.info["name"]?.toClassName() ?: "collection",
            items = postmanCollection.item.map { mapItem(it) },
            variables = postmanCollection.variable.filter { it.value != null }.associate { it.key to it.value!! }
        )

        return generatorCollection
    }

    private fun mapRequest(request: Request): org.kontr.generator.core.Request = org.kontr.generator.core.Request(
        method = request.method,
        header = mapHeaders(request.header),
        url = org.kontr.generator.core.Url(
            raw = request.url.raw,
            host = request.url.host.joinToString("/"),
            variables = if (request.url.variable == null) mutableMapOf() else request.url.variable.filter { it.value != null }
                .associate { it.key to it.value!! },
            path = request.path,
            query = request.url.query.filter { it.value != null }.associate { it.key to it.value!! },
        ),
        body = request.body?.raw
    )

    private fun mapHeaders(header: List<HeaderVariable>): Map<String, String> =
        header.filter { it.value != null }.associate { it.key to it.value!! }

    private fun mapItems(item: List<Item>): List<org.kontr.generator.core.Item> = item.map {
        org.kontr.generator.core.Item(
            name = it.name,
            request = if (it.request == null) null else mapRequest(it.request),
            items = it.item?.map { mapItem(it) },

        )
    }

    private fun mapItem(it: Item) = org.kontr.generator.core.Item(
        name = it.name,
        request = if (it.request == null) null else mapRequest(it.request),
        items = if (it.item == null) null else mapItems(it.item)
    )
}
