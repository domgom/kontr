package org.kontr.dsl

import org.kontr.dsl.Colours.BLUE
import org.kontr.dsl.Colours.END
import org.kontr.dsl.Colours.GREEN
import org.kontr.dsl.Colours.RED
import org.kontr.dsl.Colours.YELLOW
import org.kontr.dsl.Configuration.maxRequestAliasLength
import org.kontr.dsl.Configuration.prefixRQRS
import org.kontr.dsl.Configuration.useColours
import org.kontr.dsl.Configuration.useLogger
import org.kontr.dsl.Styles.ITALIC
import org.kontr.dsl.Styles.ITALIC_END
import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 * @author Domingo Gomez
 */
object Colours {
    const val RED = "\u001b[31m"
    const val BLUE = "\u001b[94m"
    const val YELLOW = "\u001b[93m"
    const val GREEN = "\u001b[32m"
    const val END = "\u001b[0m"
}

object Styles {
    const val ITALIC = "\u001B[3m"
    const val ITALIC_END = "\u001B[0m"
}

object Kontr

val logger: Logger = LoggerFactory.getLogger(Kontr::class.java)

fun Request.printRequest(requestAlias: String?) =
    flush("${prefRQ()}${requestAlias(requestAlias)}${rightArrow()} ${alias ?: "$method $url"}${body?.let { " $it" } ?: ""}".take(
        160
    ))


fun Response.printResponse(assertResult: Boolean) =
    flush("${prefRS()}${leftArrow()} ${statusCode()} ${assertResult(assertResult)} ${oneLineBody()}")

private fun flush(value: String) = with(value) { if (useLogger) logger.info(this) else println(this) }
private fun prefRQ(): String = if (prefixRQRS) "RQ " else " "
private fun prefRS(): String = if (prefixRQRS) "RS " else " "


private fun requestAlias(alias: String?): String =
    alias?.let { if (useColours) "$ITALIC$BLUE${((it).take(maxRequestAliasLength) + "()").padEnd(maxRequestAliasLength)} $END$ITALIC_END" else "$it() " }
        ?: ""

internal fun assertResult(assertResult: Boolean): String {
    return if (useColours) {
        if (assertResult) {
            "$GREEN${"Asserts✅"}$END"
        } else {
            "$RED${"Asserts❌"}$END"
        }
    } else {
        if (assertResult) "Asserts Passed" else "Asserts Failed"
    }
}


private fun Response.statusCode() = if (useColours) "[${
    when (statusCode) {
        in 200..299 -> GREEN
        in 400..599 -> RED
        else -> YELLOW
    }
}$statusCode$END]" else "[$statusCode]"

private fun rightArrow() = if (useColours) "$BLUE→$END" else "→"
private fun leftArrow() = if (useColours) "$BLUE←$END" else "←"


private fun Response.oneLineBody() = if (body.lines().size > 1) {
    body.replace(" ", "").lines().joinToString(" ")
} else {
    body
}
