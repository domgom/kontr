package org.company.example

import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.kontr.dsl.CollectionDsl
import org.kontr.dsl.collection

fun main() {
    collection {
        getReadme()
        getLicense("Apache License")
    }
}

private fun CollectionDsl.getReadme() {
    get("https://raw.githubusercontent.com/domgom/kontr/main/README.md") {
        headers { Accept("text/html") }
        onResponse { healthy }
        onResponse { assertThat(body).startsWith("# Kontr") }
    }
}

private fun CollectionDsl.getLicense(licenseName: String) {
    get("https://raw.githubusercontent.com/domgom/kontr/main/LICENSE") {
        headers { Accept("text/html") }
        onResponse {
            healthy
            assertThat(body.lines().first()).contains(licenseName)
        }
    }
}