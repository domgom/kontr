package org.company.example

import kotlinx.serialization.Serializable
import org.kontr.dsl.CollectionDsl
import org.kontr.dsl.collection

/**
 * @author Domingo Gomez
 */
fun main() {
    collection {
        println(createTodo(Todo(userId = 300, id = 201, title = "Some task to be done", completed = false)))
    }
}

@Serializable
data class Todo(val userId: Long, val id: Long, val title: String, val completed: Boolean)

private fun CollectionDsl.createTodo(todo: Todo) = post("https://jsonplaceholder.typicode.com/todos/") {
    headers {
        Accept("*/*")
        ContentType("application/json")
    }
    toJson(todo) // Object to Json serialisation
    onResponse { created }
}.fromJson<Todo>() // Json to Object deserialisation

