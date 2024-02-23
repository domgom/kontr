package org.company.example

import net.javacrumbs.jsonunit.assertj.assertThatJson
import org.assertj.core.api.Assertions.assertThat
import org.kontr.dsl.CollectionDsl
import org.kontr.dsl.collection


/**
 * @author Domingo Gomez
 */
fun main() {
    collection {
        namedRequest()
        get("https://jsonplaceholder.typicode.com/todos/2") { onResponse { ok } } // response code must be 200
        get(url = "https://jsonplaceholder.typicode.com/todos/-1") {
            onResponse { notFound } // response code must be 404
            onResponse { assertThat(body).isEqualTo("{}") } // response body assertion
        }
    }
}

private fun CollectionDsl.namedRequest() {
    get(url = "https://jsonplaceholder.typicode.com/todos/1") {
        onResponse { healthy } // codes included in 1..399
        onResponse { assertThatJson(body) { node("completed").isEqualTo(false) } } // response body json assertion
    }
}

