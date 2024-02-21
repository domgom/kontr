package org.kontr.generator.postman

import kotlinx.serialization.json.*
import org.kontr.dsl.HttpMethod
import java.io.File
import java.nio.file.Path

/**
 * @author Domingo Gomez
 */
class PostmanParser {
    fun parsePostmanCollection(collectionFilePath: Path): PostmanCollection {
        val jsonString = collectionFilePath.toFile().readText()
        val jsonObject = Json.decodeFromString<JsonObject>(jsonString)
        val infoObject = jsonObject.getValue("info").jsonObject
        val infoMap = infoObject.toMap().mapValues { it.value.jsonPrimitive.content }
        val itemsArray = jsonObject.getValue("item").jsonArray
        val items = itemsArray.map { parseCollectionItem(it.jsonObject) }

        return PostmanCollection(infoMap, items)
    }

    private fun parseCollectionItem(json: JsonObject): CollectionItem {
        val name = json.getValue("name").jsonPrimitive.content
        val request = json["request"]?.jsonObject?.let { parseRequest(it) }
        val item = json["item"]?.jsonArray?.map { parseCollectionItem(it.jsonObject) }
        return CollectionItem(name, request, item)
    }

    private fun parseRequest(json: JsonObject): Request {
        val method = HttpMethod.valueOf(json.getValue("method").jsonPrimitive.content.uppercase())
        val url = json.getValue("url").jsonObject.getValue("raw").jsonPrimitive.content
        val headers = json["header"]?.jsonArray?.associate {
            it.jsonObject.getValue("key").jsonPrimitive.content to it.jsonObject.getValue("value").jsonPrimitive.content
        } ?: emptyMap()
        val bodyMode = json["body"]?.jsonObject?.getValue("mode")?.jsonPrimitive?.content
        val body = when (bodyMode) {
            "raw" -> json["body"]?.jsonObject?.getValue("raw")?.jsonPrimitive?.content ?: ""
            else -> ""
        }
        return Request(method, url, headers, body)
    }


}