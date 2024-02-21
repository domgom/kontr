package org.kontr

import net.javacrumbs.jsonunit.assertj.assertThatJson
import org.kontr.Env2.accessToken
import org.kontr.Env2.baseUrl
import org.kontr.Env2.title
import org.kontr.dsl.DslColour4
import org.kontr.dsl.HttpMethod.POST
import org.kontr.dsl.Response
import org.kontr.dsl.collection

/**
 * @author Domingo Gomez
 */
data object Env2 {
    const val title: String = "Title"
    const val baseUrl: String = "https://jsonplaceholder.typicode.com"
    var accessToken: String = "Bearer xxx"
}

fun main() {
    println("Kontr! Sync example")

    collection {

        requestWithAlias()
        run {
            println("Using env: $title")
        }

        get("$baseUrl/users") {
            headers { Accept("application/json") }
            onResponse { ok }
        }

        post("$baseUrl/users") {
            body = """{}"""
            headers { Accept("application/json") }
            onResponse { ok }
        }

        rq(url = "$baseUrl/posts", method = POST) {
            //Different ways of initialising headers
            headers["Accept"] = listOf("application/json", "text/html")
            header("Accept", "application/json", "text/html")
            headers {
                Authorization("Bearer $accessToken")
                ContentType("application/json")
            }
            body = """
                "title" : "$title",
                "body" : "bar",
                "userId" : 1 
            """
            run {
                println("Awaiting before assertion of request $method to $url")
                Thread.sleep(1000)
            }
            onResponse {
                healthy
                assertThatJson(body) {
                    node("name").isEqualTo("John")
                }
                jpath("$.name", "John")
            }
        }

        requestWithAlias()
    }
}

// Json Unit is not included as a dependency in kontr-dsl, this extension method makes it more friendly
@DslColour4
fun Response.jpath(path: String, value: Any) = assertThatJson(body) {
    inPath(path).isEqualTo(value)
}