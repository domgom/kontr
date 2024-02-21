import org.junit.jupiter.api.Test
import org.kontr.dsl.collection

/**
 * @author Domingo Gomez
 */
class ReportsTest {
    @Test
    fun `test report of failures ok`() {
        try {
            val col = collection {
                get("https://jsonplaceholder.typicode.com/todos/1") { onResponse { ok; healthy } }
                get("https://jsonplaceholder.typicode.com/todos/1") { onResponse { badRequest } }
                get("https://jsonplaceholder.typicode.com/todos/1") { onResponse { ok } }
                get("https://jsonplaceholder.typicode.com/todos/1") { onResponse { internalError } }
            }

        } catch (e: AssertionError) {
            println("DONE")
        }
    }
}