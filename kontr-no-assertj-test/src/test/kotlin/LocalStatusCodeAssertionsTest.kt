import org.kontr.dsl.collection
import kotlin.test.Test
import kotlin.test.assertFailsWith

/**
 * @author Domingo Gomez
 */
class LocalStatusCodeAssertionsTest {
    @Test
    fun assertEquals() {
        collection {
            get("https://jsonplaceholder.typicode.com/todos/1") { onResponse { ok } }
        }
    }

    @Test
    fun assertIsNotEquals() {
        throwsAssertionWithError("200 is not equals to 400") {
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
        throwsAssertionWithError("404 is not between 200..299") {
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
        throwsAssertionWithError("404 is not less than 400") {
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

