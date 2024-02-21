package org.kontr

import net.javacrumbs.jsonunit.assertj.assertThatJson
import org.assertj.core.api.Assertions.assertThat
import org.kontr.dsl.CollectionDsl
import org.kontr.dsl.collection


/**
 * @author Domingo Gomez
 */
fun main() {
    collection {
        requestWithAliasTypicode()
        get("https://jsonplaceholder.typicode.com/todos/2") { onResponse { badRequest } }
        get(url = "https://jsonplaceholder.typicode.com/todos/3") { onResponse { ok }; onResponse { healthy } }
        get(url = "https://jsonplaceholder.typicode.com/todos/4") {
            onResponse { internalError }
            onResponse {
                assertThat(body).isEqualTo("")
            }
        }
    }
}

private fun CollectionDsl.requestWithAliasTypicode() {
    get(url = "https://jsonplaceholder.typicode.com/todos/1") {
        onResponse { ok; healthy }
        onResponse { assertThatJson(body) { node("completed").isEqualTo(false) } }
    }
}

