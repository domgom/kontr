import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.kontr.dsl.Collection
import org.kontr.dsl.collection
import org.kontr.http.client.java.SyncJavaHttpClient

private const val URL = "https://jsonplaceholder.typicode.com/todos/1"
private const val H1_KEY = "Content-Type"
private const val H1_VALUE = "application/json"
private const val H2_KEY = "Accept"
private const val H2_VALUE1 = "application/json"
private const val H2_VALUE2 = "text/plain"
private const val H3_KEY = "Custom"
private const val H3_VALUE1 = "MyCustomValue1"
private const val H3_VALUE2 = "MyCustomValue2"

/**
 * @author Domingo Gomez
 */
class HeadersTest {
    @Test
    fun `test GET request with Content-Type headers{ }`() = verifyContentType(collection {
        client(SyncJavaHttpClient())
        get(URL) { headers { ContentType(H1_VALUE) } }
    })

    @Test
    fun `test GET request with Accept headers{ }`() = verifyAccept(collection {
        client(SyncJavaHttpClient())
        get(URL) { headers { Accept(H2_VALUE1, H2_VALUE2) } }
    })

    @Test
    fun `test GET request with headers{ Header(Content-Type) }`() = verifyContentType(collection {
        client(SyncJavaHttpClient())
        get(URL) { headers { Header(H1_KEY, H1_VALUE) } }
    })

    @Test
    fun `test GET request with headers{ Header(Accept) }`() = verifyAccept(collection {
        client(SyncJavaHttpClient())
        get(URL) { headers { Header(H2_KEY, H2_VALUE1, H2_VALUE2) } }
    })

    @Test
    fun `test GET request with Content-Type header()`() = verifyContentType(collection {
        client(SyncJavaHttpClient())
        get(URL) { header(H1_KEY, H1_VALUE) }
    })

    @Test
    fun `test GET request with Accept header()`() = verifyAccept(collection {
        client(SyncJavaHttpClient())
        get(URL) { header(H2_KEY, H2_VALUE1, H2_VALUE2) }
    })

    @Test
    fun `test GET request with headers attribute set Content-Type`() = verifyContentType(collection {
        client(SyncJavaHttpClient())
        get(URL) { headers[H1_KEY] = listOf(H1_VALUE) }
    })

    @Test
    fun `test GET request with headers attribute set Accept`() = verifyAccept(collection {
        client(SyncJavaHttpClient())
        get(URL) { headers[H2_KEY] = listOf(H2_VALUE1, H2_VALUE2) }
    })

    @Test
    fun `test GET request with headers{ Header(Custom), Accept(), Content-Type() }`() {
        val col = collection {
            client(SyncJavaHttpClient())
            get(URL) {
                headers {
                    Header(H1_KEY, H1_VALUE)
                    Header(H2_KEY, H2_VALUE1, H2_VALUE2)
                    Header(H3_KEY, H3_VALUE1, H3_VALUE2)
                }
            }
        }
        verifyCustomAndContentTypeAndAccept(col)
    }

    private fun verifyCustomAndContentTypeAndAccept(col: Collection) =
        verify(
            col, mutableMapOf(
                H1_KEY to listOf(H1_VALUE),
                H2_KEY to listOf(H2_VALUE1, H2_VALUE2),
                H3_KEY to listOf(H3_VALUE1, H3_VALUE2)
            )
        )

    private fun verifyContentType(col: Collection) = verify(col, mutableMapOf(H1_KEY to listOf(H1_VALUE)))
    private fun verifyAccept(col: Collection) = verify(col, mutableMapOf(H2_KEY to listOf(H2_VALUE1, H2_VALUE2)))
    private fun verify(c: Collection, headers: MutableMap<String, List<String>>) {
        with(c.requests) {
            assertThat(size).isEqualTo(1)
            assertThat(get(0).headers).isEqualTo(headers)
        }

        with(c.responses) {
            assertThat(size).isEqualTo(1)
            assertThat(get(0).body).isEqualTo(
                """
                {
                  "userId": 1,
                  "id": 1,
                  "title": "delectus aut autem",
                  "completed": false
                }
            """.trimIndent()
            )
        }
    }
}
