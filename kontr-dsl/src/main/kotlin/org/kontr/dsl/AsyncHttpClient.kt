package org.kontr.dsl

/**
 * @author Domingo Gomez
 */
interface AsyncHttpClient {
    suspend fun execute(request: Request): Response
}