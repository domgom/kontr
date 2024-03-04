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
    val queryParams: MutableMap<String, String> = mutableMapOf()
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

    /**
     * Sends the query param allowing a nullable value. By default (sendEmptyValue=false) null values are omitted.
     * If sendEmptyValue=true, the url will contain "baseUri?param1=&param2=value2"
     *
     * @param key the query param key (or name)
     * @param value the nullable query param value
     * @param sendEmptyValue the flag to force sending null the query param name with empty String value ("") or not.
     * @return Unit
     */
    @DslColour1
    fun queryParam(key: String, value: String?, sendEmptyValue: Boolean = false) {
        if (sendEmptyValue) {
            queryParams[key] = value ?: ""
        } else if (!value.isNullOrEmpty()) {
            queryParams[key] = value
        }
    }

    @DslColour1
    fun <T : Any> queryParam(key: String, multiValue: Iterable<T>, separator: String = ",") {
        val separatedValues = multiValue.joinToString(separator)
        if (separatedValues.isNotEmpty()) {
            queryParams[key] = separatedValues
        }
    }

    @DslColour1
    fun <T : Any> queryParam(key: String, value: T) {
        queryParams[key] = value.toString()
    }

    @DslColour1
    fun <T : Any> queryParams(vararg pairs: Pair<String, T>) {
        pairs.forEach { (key, value) -> queryParams[key] = value.toString() }
    }

    @DslColour1
    fun queryParams(vararg entries: String) {
        require(entries.size % 2 == 0) { "Number of arguments must be even" }
        for (i in entries.indices step 2) {
            val key = entries[i]
            val value = entries[i + 1]
            queryParams[key] = value
        }
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

