package org.kontr.generator.postman

import kotlinx.serialization.json.*
import org.kontr.dsl.HttpMethod
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
        //change host to List<String> ?
        val host = json.getValue("url").jsonObject.getValue("host")?.jsonArray?.map {
            it.jsonPrimitive.content
        }?.toList() ?: emptyList()
        val path = json.getValue("url").jsonObject.getValue("path")?.jsonArray?.map {
            it.jsonPrimitive.content
        }?.toList() ?: emptyList()

        val headers = json.getValue("header").jsonArray.associate {
            it.jsonObject.getValue("key").jsonPrimitive.content to it.jsonObject.getValue("value").jsonPrimitive.content
        }
        val query = readMapInUrlOptionalChildren(json, "query")
        val variables = readMapInUrlOptionalChildren(json, "variable")
        val bodyMode = json["body"]?.jsonObject?.getValue("mode")?.jsonPrimitive?.content
        val body = when (bodyMode) {
            "raw" -> json["body"]?.jsonObject?.getValue("raw")?.jsonPrimitive?.content ?: ""
            else -> ""
        }
        return Request(method, url, host, path, query, headers, variables, body)
    }

    private fun readMapInUrlOptionalChildren(json: JsonObject, childName: String) =
        if (json.getValue("url").jsonObject.containsKey(childName))
            json.getValue("url").jsonObject.getValue(childName)?.jsonArray?.associate {
                it.jsonObject.getValue("key").jsonPrimitive.content to it.jsonObject.getValue("value").jsonPrimitive.content
            } ?: emptyMap() else emptyMap()


}