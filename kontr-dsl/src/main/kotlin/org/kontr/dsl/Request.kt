package org.kontr.dsl

/**
 * @author Domingo Gomez
 */
open class Request {
    var alias: String? = null
    var method: HttpMethod = HttpMethod.GET
    var url: String = ""
    val headers: MutableMap<String, List<String>> = mutableMapOf()
    var body: String? = null
    private val assertions = mutableListOf<ResponseDsl.() -> Unit>()
    private var preScript: RequestDsl.() -> Unit = {}

    @DslColour1
    fun preScript(script: RequestDsl.() -> Unit) {
        preScript = script
    }

    internal fun evalPreScript() = (this as RequestDsl).apply(preScript)

    @DslColour1
    fun onResponse(assertion: ResponseDsl.() -> Unit) = assertions.add(assertion)
    internal fun evalAssertions(response: ResponseDsl) {
        assertions.forEach { response.apply(it) }
    }

    @DslColour1
    fun header(key: String, vararg values: String) {
        headers[key] = values.toList()
    }

    @DslColour1
    fun headers(block: HttpHeadersBuilder.() -> HttpHeader) {
        val builder = HttpHeadersBuilder()
        builder.block()
        builder.build().values.forEach {
            this@Request.headers[it.key] = it.values.toList()
        }
    }

}
