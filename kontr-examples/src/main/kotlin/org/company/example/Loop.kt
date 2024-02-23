package org.company.example

import org.kontr.dsl.collection


/**
 * @author Domingo Gomez
 */
// Kotlin's do while
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

// Kotlin's do while with iterations limit
fun conditionAndMax20Retries() {
    collection {
        var completed = false
        var id = 1
        do {
            get(url = "https://jsonplaceholder.typicode.com/todos/${id++}") {
                onResponse { healthy; completed = (id >= 20) || body.contains("\"completed\": true") }
            }
        } while (!completed)
    }
}

// Kontr's `repeat`
fun repeat() {
    collection {
        var id = 1
        repeat(10) { get(url = "https://jsonplaceholder.typicode.com/todos/${id++}") { onResponse { healthy } } }
    }
}

// Kontr's `until(condition, request receiver)` stops on condition satisfied
fun untilConditionTest() {
    collection {
        var id = 1
        until({ body.contains("\"completed\": true") }) {
            get(url = "https://jsonplaceholder.typicode.com/todos/${id++}") { onResponse { healthy } }
        }
    }
}

// Kontr's `until(maxTimes, condition, request receiver)` stops when either condition is satisfied OR repeat maxTimes is reached
fun main() {
    collection {
        var id = 1
        until(2, { body.contains("\"completed\": true") }) {
            get(url = "https://jsonplaceholder.typicode.com/todos/${id++}") { onResponse { healthy } }
        }
    }
}


