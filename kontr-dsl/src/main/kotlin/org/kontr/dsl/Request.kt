package org.kontr.dsl

import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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

    /**
     * Serialises the object using kotlinx serialisation. This dependency is scoped as provided so you need to supply it
     * if you wish to use this method, otherwise it will result in runtime errors.
     *
     * @param  bodyObject The object of type [T] to write as json body. It needs to be @Serializable
     * @return An json string of the serialised body object.
     * @throws NoClassDefFoundError in case of kotlinx serialisation libraries not available in classpath
     * @throws SerializationException in case of any decoding-specific error
     * @throws IllegalArgumentException if the decoded input is not a valid instance of [T]
     */
    @DslColour3
    inline fun <reified T> toJson(bodyObject: T): String = Json.encodeToString(bodyObject).apply { body = this }
}

