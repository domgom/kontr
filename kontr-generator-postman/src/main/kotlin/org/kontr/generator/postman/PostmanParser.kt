package org.kontr.generator.postman

import kotlinx.serialization.json.Json
import org.kontr.generator.core.GeneratorCollection
import org.kontr.generator.core.IParser
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Domingo Gomez
 */
class PostmanParser : IParser {
    private val json = Json { ignoreUnknownKeys = true }
    override fun parseGeneratorCollection(inputPath: String): GeneratorCollection {
        val postmanCollection = parsePostmanCollection(Paths.get(inputPath))
        val generatorCollection = mapCollection(postmanCollection)
        return generatorCollection
    }

    private fun parsePostmanCollection(collectionFilePath: Path): PostmanCollection {
        val postmanCollection = json.decodeFromString<PostmanCollection>(collectionFilePath.toFile().readText())
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
            variables = postmanCollection.variable.associate { it.key to it.value }
        )

        return generatorCollection
    }

    private fun mapRequest(request: Request): org.kontr.generator.core.Request = org.kontr.generator.core.Request(
        method = request.method,
        header = mapHeaders(request.header),
        url = org.kontr.generator.core.Url(
            raw = request.url.raw,
            host = request.url.host.joinToString("/"),
            variables = if (request.url.variable == null) mutableMapOf() else request.url.variable.associate { it.key to it.value },
            path = request.path,
            query = request.url.query.associate { it["key"]!! to it["value"]!! },
        ),
        body = request.body?.raw
    )

    private fun mapHeaders(header: List<Map<String, String>>): Map<String, String> =
        header.associate { it["key"]!! to it["value"]!! }

    private fun mapItems(item: List<Item>): List<org.kontr.generator.core.Item> = item.map {
        org.kontr.generator.core.Item(
            name = it.name,
            items = it.item?.map { mapItem(it) }
        )
    }

    private fun mapItem(it: Item) = org.kontr.generator.core.Item(
        name = it.name,
        request = if (it.request == null) null else mapRequest(it.request),
        items = if (it.item == null) null else mapItems(it.item)
    )
}
