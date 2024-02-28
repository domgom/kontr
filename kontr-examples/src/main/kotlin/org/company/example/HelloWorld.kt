package org.company.example

import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.kontr.dsl.CollectionDsl
import org.kontr.dsl.collection

fun main() {
    collection {
        checkReadme()
        verifyLicense("Apache License")
    }
}

private fun CollectionDsl.checkReadme() {
    get("https://raw.githubusercontent.com/domgom/kontr/main/README.md") {
        headers { Accept("text/html") }
        onResponse { healthy }
        onResponse { assertThat(body).startsWith("![Kontr logo") }
    }
}

private fun CollectionDsl.verifyLicense(licenseName: String) {
    get("https://raw.githubusercontent.com/domgom/kontr/main/LICENSE") {
        headers { Accept("text/html") }
        onResponse {
            healthy
            assertThat(body.lines().first()).contains(licenseName)
        }
    }
}