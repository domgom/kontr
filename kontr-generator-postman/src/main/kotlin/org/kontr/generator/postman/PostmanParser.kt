package org.kontr.generator.postman

import kotlinx.serialization.json.*
import org.kontr.dsl.HttpMethod
import java.nio.file.Path

/**
 * @author Domingo Gomez
 */
class PostmanParser {
    fun parsePostmanCollection(collectionFilePath: Path): PostmanCollection {

        val  postmanCollection = Json.decodeFromString<PostmanCollection>(collectionFilePath.toFile().readText())

        return PostmanCollection(postmanCollection.info, postmanCollection.item)
    }
}