package org.kontr.web.views

import kotlinx.html.*
import kotlinx.html.InputType.file

/**
 * @author Domingo Gomez
 */
fun HTML.index() {
    classes = setOf("h-full", "bg-gray-100")
    head {
        title {
            +"Kontr Web Generator"
        }
        script { src = "https://cdn.tailwindcss.com/3.4.1" }
        script { src = "https://unpkg.com/htmx.org@1.9.10/dist/htmx.min.js" }
    }
    body {
        classes = setOf("h-full")
        div {
            classes = setOf("min-h-ful")
            loadFile()
        }
    }
}

fun FlowContent.loadFile() {
    div {
        span { +"Select your postman collection file" }
        form {
            id = "form"
            attributes["hx-encoding"] = "multipart/form-data"
            attributes["hx-post"] = "/upload2"
            input {
                type = file
                name = "file"
            }
            button { +"Upload" }
            progress {
                id = "progress"
                value = "0"
                max = "100"
            }
        }
        script {
            +"""htmx.on('#form', 'htmx:xhr:progress', function(evt) {
          htmx.find('#progress').setAttribute('value', evt.detail.loaded/evt.detail.total * 100)
        });"""
        }
    }
}

fun FlowContent.boxed(text: String) {
    div {
        span { +"Collection converted to Kontr DSL!" }
        div { +text }
    }
}



