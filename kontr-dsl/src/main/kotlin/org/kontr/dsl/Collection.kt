package org.kontr.dsl

import org.kontr.dsl.HttpMethod.*
import org.kontr.http.client.java.SyncJavaHttpClient

/**
 * @author Domingo Gomez
 */
@DslColour4
fun collection(builder: CollectionDsl.() -> Unit): CollectionDsl = CollectionDsl().apply(builder)

open class Collection {
    private var client: SyncHttpClient = SyncJavaHttpClient()
    val requests = mutableListOf<Request>()
    val responses = mutableListOf<Response>()

    @DslColour2
    fun client(client: SyncHttpClient) {
        this.client = client
    }

    private fun buildRequestInternal(
        uri: String,
        httpMethod: HttpMethod,
        builder: RequestDsl.() -> Unit,
        shouldFailOnBody: Boolean = false,
    ): ResponseDsl =
        processRequest(
            RequestDsl().apply { url = uri; method = httpMethod }
                .apply(builder)
                .also { this@Collection.failOnBody(shouldFailOnBody, it.body) })


    fun List<RequestDsl>.toRqs() = forEach { processRequest(it) }

    // This function may be duplicating requests if people use it to declare inside the collection dsl like : rqs{ get{} get{}}
    @DslColour1
    fun rqs(function: () -> List<RequestDsl>) = function().forEach { processRequest(it) }

    @DslColour1
    fun rq(url: String, method: HttpMethod, builder: RequestDsl.() -> Unit): ResponseDsl =
        buildRequestInternal(url, method, builder)

    private fun processRequest(rq: RequestDsl): ResponseDsl {
        rq.evalPreScript()
        requests.add(rq)
        val (callingMethodName, classLine) = getCallingMethod()
        rq.printRequest(callingMethodName, classLine)
        val rs = client.execute(rq)
        responses.add(rs)
        try {
            rq.evalAssertions(rs)
            rs.printResponse(true)
        } catch (e: AssertionError) {
            rs.printResponse(false)
            if (Configuration.stopOnAssertionError) {
                throw e
            }
        }
        return rs
    }

    @DslColour2
    fun get(url: String, builder: RequestDsl.() -> Unit): ResponseDsl =
        buildRequestInternal(url, GET, builder, true)

    @DslColour4
    fun post(url: String, builder: RequestDsl.() -> Unit): ResponseDsl =
        buildRequestInternal(url, POST, builder)

    private fun getCallingMethod(): Pair<String?, String?> {
        val stackTrace = Thread.currentThread().stackTrace
        for (i in 4 until minOf(8, stackTrace.size)) {
            val methodName = stackTrace[i].methodName
            val classLine = "${stackTrace[i].fileName}:${stackTrace[i].lineNumber}"

            if (!stackTrace[i].className.startsWith(Collection::class.java.canonicalName)) {
                return if (methodName == "invoke") Pair(null, null) else Pair(methodName, classLine)
            }
        }
        return Pair(null, null)
    }

    @DslColour4
    fun put(url: String, builder: RequestDsl.() -> Unit): ResponseDsl =
        buildRequestInternal(url, PUT, builder)

    @DslColour4
    fun patch(url: String, builder: RequestDsl.() -> Unit): ResponseDsl =
        buildRequestInternal(url, PATCH, builder)

    @DslColour4
    fun delete(url: String, builder: RequestDsl.() -> Unit): ResponseDsl =
        buildRequestInternal(url, DELETE, builder, true)

    @DslColour1
    fun options(url: String, builder: RequestDsl.() -> Unit): ResponseDsl =
        buildRequestInternal(url, OPTIONS, builder, true)

    @DslColour1
    fun head(url: String, builder: RequestDsl.() -> Unit): ResponseDsl =
        buildRequestInternal(url, HEAD, builder, true)

    @DslColour1
    fun trace(url: String, builder: RequestDsl.() -> Unit): ResponseDsl =
        buildRequestInternal(url, TRACE, builder, true)

    @DslColour1
    fun query(url: String, builder: RequestDsl.() -> Unit): ResponseDsl =
        buildRequestInternal(url, QUERY, builder, true)

    private fun failOnBody(shouldFailOnBody: Boolean, body: String?): Unit? {
        return if (shouldFailOnBody)
            body?.let { throw RuntimeException("HTTP Method does not support a body. See RFC 9110") }
        else Unit
    }

    @DslColour3
    fun repeat(times: Int, requestBlock: () -> ResponseDsl) {
        for (i in 0..times) {
            requestBlock()
        }
    }

    @DslColour3
    fun until(condition: ResponseDsl.() -> Boolean, requestBlock: () -> ResponseDsl) {
        do {
            requestBlock()
        } while (!condition(responses.last() as ResponseDsl))

    }

    @DslColour3
    fun until(
        maxTimes: Int,
        condition: ResponseDsl.() -> Boolean,
        requestBlock: () -> ResponseDsl
    ) {
        var i = 1
        do {
            requestBlock()
        } while (!condition(responses.last() as ResponseDsl) && i++ < maxTimes)
    }
}


