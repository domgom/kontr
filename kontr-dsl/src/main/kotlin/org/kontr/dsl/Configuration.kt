package org.kontr.dsl

/**
 * @author Domingo Gomez
 */
data object Configuration {
    var stopOnAssertionError: Boolean = true // When true: stops further requests after an assertion error
    var useColours: Boolean = true // When true: prints special characters for console coloured output
    var prefixRQRS: Boolean = false // When true: prints RQ/RS at the beginning of each line
    var printCallingClassAndLine = true //It prints a clickable name in intelliJ of the caller class (if not anonymous and maxRequestAliasLength space left)
    var maxRequestAliasLength: Int = 40
    var maxRequestBodyLength: Int = 200
    var maxResponseBodyLength: Int = 200
    var useLogger: Boolean = false // When true: prints using slf4j logger instead of direct console output
    var defaultOnResponseAssertion: String? = "onResponse{ healthy }" // null for skipping onResponse entirely
}