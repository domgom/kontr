package org.kontr.org.company.example

import net.javacrumbs.jsonunit.assertj.assertThatJson
import org.kontr.dsl.DslColour4
import org.kontr.dsl.Response
import org.kontr.dsl.collection
import org.kontr.org.company.example.EnvVars.BASE_URL
import org.kontr.org.company.example.EnvVars.token

/**
 * @author Domingo Gomez
 */
data object EnvVars {
    const val BASE_URL = "https://echo.free.beeceptor.com/weather-api" // Your real API under test
    var token: String = "EncodedToken"
}

fun main() {
    val cityId = 1_542
    val cityName = "San Francisco"
    val date = "2024-07-01T00:00:00Z"

    collection {
        get("$BASE_URL/current") {
            queryParam("city", cityName)
            onResponse { ok }
            onResponse {
                assertJpath("method", "GET")
                assertJpath("path", "/weather-api/current?city=San%20Francisco")
            }
        }
        val forecast = get("$BASE_URL/forecast") {
            queryParam("daysAhead", "5")
            onResponse { healthy }
        }.body
        println("Forecast Length: ${forecast.length}")

        put("$BASE_URL/weather/$cityId/$date") {
            headers {
                Authorization("Bearer $token")
                Accept("application/json")
            }
            onResponse { in2XX }
            body = """{'temperature': 25, 'conditions': 'Partly Cloudy'}"""
        }
    }
}

// Json Unit is not included as a dependency in kontr-dsl, this extension method makes it more friendly to use
@DslColour4
fun Response.assertJpath(path: String, value: Any) = assertThatJson(body) {
    inPath(path).isEqualTo(value)
}