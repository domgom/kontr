package org.kontr

import org.kontr.dsl.CollectionDsl
import org.kontr.dsl.RequestDsl

/**
 * @author Domingo Gomez
 */
fun CollectionDsl.requestWithAlias(): RequestDsl = post("${Env2.baseUrl}/posts") {
    header("Content-Type", "application/json")
    header("Accept", "*/*")
    header("Bearer", Env2.accessToken)
    body = """
                "title" : "${Env2.accessToken}",
                "body" : "bar",
                "userId" : 1 
            """
}
