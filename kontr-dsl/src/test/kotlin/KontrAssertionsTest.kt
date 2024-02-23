import org.kontr.asserts.assertThat
import kotlin.test.Test
import kotlin.test.assertFailsWith

/**
 * @author Domingo Gomez
 */
class KontrAssertionsTest {
    @Test
    fun assertEquals() {
        assertThat(200).isEqualTo(200)
    }

    @Test
    fun assertIsNotEquals() {
        throwsAssertionWithError("\nexpected: 400\n but was: 200") {
            assertThat(200).isEqualTo(400)
        }
    }

    @Test
    fun assertIsBetween() {
        assertThat(200).isBetween(200, 299)
    }

    @Test
    fun assertIsNotBetween() {
        throwsAssertionWithError("\nExpecting actual:\n  404\nto be between:\n  [200, 299]\n") {
            assertThat(404).isBetween(200, 299)
        }
    }

    @Test
    fun assertIsLessThan() {
        assertThat(399).isLessThan(400)
    }

    @Test
    fun assertIsNotLessThan() {
        throwsAssertionWithError("\nExpecting actual:\n  404\nto be less than:\n  400 ") {
            assertThat(404).isLessThan(400)
        }
    }
}

internal fun throwsAssertionWithError(message: String, lambda: Any.() -> Unit) {
    val error = assertFailsWith<AssertionError> {
        Any().apply(lambda)
    }
    if (error.message != message) {
        throw error
    }
}