package org.kontr.dsl

/**
 * @author Domingo Gomez
 */
data object Configuration {
    var stopOnAssertionError: Boolean = true // When true: stops further requests after an assertion error
    var useColours: Boolean = true // When true: prints special characters for console coloured output
    var prefixRQRS: Boolean = false // When true: prints RQ/RS at the beginning of each line
    var maxRequestAliasLength: Int = 30
    var useLogger: Boolean = false // When true: prints using slf4j logger instead of direct console output
}