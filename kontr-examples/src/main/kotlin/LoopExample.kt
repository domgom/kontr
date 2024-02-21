package org.kontr

import org.kontr.dsl.collection


/**
 * @author Domingo Gomez
 */
fun condition() {
    collection {
        var completed = false
        var id = 1
        do {
            get(url = "https://jsonplaceholder.typicode.com/todos/${id++}") {
                onResponse {
                    healthy
                    completed = body.contains("\"completed\": true")
                }
            }
        } while (!completed)
    }
}


fun conditionAndMax20Retries() {
    collection {
        var completed = false
        var id = 1
        do {
            get(url = "https://jsonplaceholder.typicode.com/todos/${id++}") {
                onResponse { healthy; completed = id >= 20 || body.contains("\"completed\": true") }
            }
        } while (!completed)
    }
}


fun repeat() {
    collection {
        var id = 1
        repeat(10) { get(url = "https://jsonplaceholder.typicode.com/todos/${id++}") { onResponse { healthy } } }
    }
}

fun untilConditionTest() {
    collection {
        var id = 1
        until({ body.contains("\"completed\": true") }) {
            get(url = "https://jsonplaceholder.typicode.com/todos/${id++}") { onResponse { healthy } }
        }
    }
}

//repeatUntilConditionTest
fun main() {
    collection {
        var id = 1
        until(2, { body.contains("\"completed\": true") }) {
            get(url = "https://jsonplaceholder.typicode.com/todos/${id++}") { onResponse { healthy } }
        }
    }
}


