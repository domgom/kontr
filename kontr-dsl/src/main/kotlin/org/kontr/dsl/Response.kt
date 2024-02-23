package org.kontr.dsl

import org.kontr.asserts.assertThat
import java.net.HttpURLConnection.*

/**
 * @author Domingo Gomez
 */
@DslColour4
open class Response(
    var statusCode: Int = 0,
    var headers: Map<String, List<String>> = mapOf(),
    var body: String = ""
) {
    @DslColour2
    fun status(status: Int) = assertThat(statusCode).isEqualTo(status)

    @DslColour2
    val ok get() = status(HTTP_OK)

    @DslColour2
    val created get() = status(HTTP_CREATED)

    @DslColour2
    val accepted get() = status(HTTP_ACCEPTED)

    @DslColour2
    val noContent get() = status(HTTP_NO_CONTENT)

    @DslColour2
    val in2XX
        get() = assertThat(statusCode).isBetween(200, 299)

    @DslColour2
    val healthy
        get() = assertThat(statusCode).isLessThan(HTTP_BAD_REQUEST)

    @DslColour4
    val badRequest get() = status(HTTP_BAD_REQUEST)

    @DslColour4
    val notFound get() = status(HTTP_NOT_FOUND)

    @DslColour4
    val internalError get() = status(HTTP_INTERNAL_ERROR)
}

