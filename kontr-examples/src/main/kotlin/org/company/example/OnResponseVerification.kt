package org.company.example

import net.javacrumbs.jsonunit.assertj.assertThatJson
import org.assertj.core.api.Assertions.assertThat
import org.kontr.dsl.collection

/**
 * @author Domingo Gomez
 */
fun main() {
    collection {
        get("https://jsonplaceholder.typicode.com/todos/2") {
            // Different ways of asserting the status code
            onResponse { ok } // response status code must be 200
            onResponse { healthy } // response status codes included in 1..399
            onResponse { status(200) } // response status code must be 200
        }
        get(url = "https://jsonplaceholder.typicode.com/todos/-1") {
            onResponse { notFound } // response code must be 404
            onResponse { assertThat(body).isEqualTo("{}") } // response body assertion
        }
        get(url = "https://jsonplaceholder.typicode.com/todos/1") {
            onResponse { assertThatJson(body) { node("completed").isEqualTo(false) } } // response body json assertion
        }
    }
}

