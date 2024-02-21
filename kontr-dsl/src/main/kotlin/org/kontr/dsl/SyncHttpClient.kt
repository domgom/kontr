package org.kontr.dsl

/**
 * @author Domingo Gomez
 */
interface SyncHttpClient {
    fun execute(request: RequestDsl): ResponseDsl
}