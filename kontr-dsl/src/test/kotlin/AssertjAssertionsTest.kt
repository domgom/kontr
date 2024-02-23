import org.kontr.dsl.collection
import kotlin.test.Test
import kotlin.test.assertFailsWith

/**
 * @author Domingo Gomez
 * Tests for assertj-core included in the classpath
 */
class AssertjAssertionsTest {
    @Test
    fun assertEquals() {
        collection {
            get("https://jsonplaceholder.typicode.com/todos/1") { onResponse { ok } }
        }
    }

    @Test
    fun assertIsNotEquals() {
        throwsAssertionWithError("\nexpected: 400\n but was: 200") {
            collection {
                get("https://jsonplaceholder.typicode.com/todos/1") { onResponse { badRequest } }
            }
        }
    }

    @Test
    fun assertIsBetween() {
        collection {
            get(url = "https://jsonplaceholder.typicode.com/todos/1") { onResponse { in2XX } }
        }
    }

    @Test
    fun assertIsNotBetween() {
        throwsAssertionWithError("\nExpecting actual:\n  404\nto be between:\n  [200, 299]\n") {
            collection {
                get(url = "https://jsonplaceholder.typicode.com/todos/-1") { onResponse { in2XX } }
            }
        }
    }

    @Test
    fun assertIsLessThan() {
        collection {
            get(url = "https://jsonplaceholder.typicode.com/todos/1") { onResponse { healthy } }
        }
    }

    @Test
    fun assertIsNotLessThan() {
        throwsAssertionWithError("\nExpecting actual:\n  404\nto be less than:\n  400 ") {
            collection {
                get(url = "https://jsonplaceholder.typicode.com/todos/-1") { onResponse { healthy } }
            }
        }
    }

    private fun throwsAssertionWithError(message: String, lambda: Any.() -> Unit) {
        val error = assertFailsWith<AssertionError> {
            apply(lambda)
        }
        if (error.message != message) {
            throw error
        }
    }
}

