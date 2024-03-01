package org.kontr.dsl

import org.kontr.dsl.Colours.BLUE
import org.kontr.dsl.Colours.END
import org.kontr.dsl.Colours.GREEN
import org.kontr.dsl.Colours.L_GRAY
import org.kontr.dsl.Colours.PURPLE
import org.kontr.dsl.Colours.RED
import org.kontr.dsl.Colours.YELLOW
import org.kontr.dsl.Configuration.maxRequestAliasLength
import org.kontr.dsl.Configuration.maxResponseBodyLength
import org.kontr.dsl.Configuration.prefixRQRS
import org.kontr.dsl.Configuration.printCallingClassAndLine
import org.kontr.dsl.Configuration.useColours
import org.kontr.dsl.Configuration.useLogger
import org.kontr.dsl.Styles.BOLD
import org.kontr.dsl.Styles.ITALIC
import org.kontr.dsl.Styles.ITALIC_END
import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 * @author Domingo Gomez
 */
object Colours { // https://dev.to/ifenna__/adding-colors-to-bash-scripts-48g4
    const val WHITE = "\u001b[97m"
    const val RED = "\u001b[31m"
    const val BLUE = "\u001b[94m"
    const val YELLOW = "\u001b[93m"
    const val GREEN = "\u001b[32m"
    const val PURPLE = "\u001b[35m"
    const val END = "\u001b[0m"
    const val GRAY = "\u001B[90m"
    const val L_GRAY = "\u001B[37m"
    const val L_MAGENTA = "\u001B[95m"
    const val BLACK = "\u001B[30m"
}

object Styles {
    const val BOLD = "\u001B[1m"
    const val ITALIC = "\u001B[3m"
    const val ITALIC_END = "\u001B[0m"
}

object Kontr

val logger: Logger = LoggerFactory.getLogger(Kontr::class.java)

fun Request.printRequest(requestAlias: String?, classLine: String?) =
    flush(
        "${prefRQ()}${
            requestAlias(
                requestAlias,
                classLine
            )
        }${rightArrow()} ${alias ?: "$method $url"} ${requestBody()}"
    )

fun Response.printResponse(assertResult: Boolean) =
    flush("${prefRS()}${leftArrow()} ${statusCode()} ${assertResult(assertResult)} ${responseBody()}")

fun Request.requestBody(): String =
    if (body == null) "" else {
        if (hasJsonBody()) body!!.singleLineBody() else body!!.replace("\n", "")
    }

fun Response.responseBody(): String = if (hasJsonBody()) body.singleLineBody() else body.replace("\n", "")
fun Response.hasJsonBody(): Boolean =
    "application/json" in (headers["Content-Type"] ?: headers["content-type"] ?: emptyList())

fun Request.hasJsonBody(): Boolean =
    "application/json" in (headers["Content-Type"] ?: headers["content-type"] ?: emptyList())

private fun flush(value: String) = with(value) { if (useLogger) logger.info(this) else println(this) }
private fun prefRQ(): String = if (prefixRQRS) "RQ " else " "
private fun prefRS(): String = if (prefixRQRS) "RS " else " "


private fun requestAlias(alias: String?, classLine: String?): String = alias?.let {
    if (useColours) {
        if (it.length + (classLine?.length ?: 0) + 3 <= maxRequestAliasLength) {
            "$ITALIC$BLUE${(it + "()" + END + ITALIC_END + classLine(classLine)).padEnd(maxRequestAliasLength + 10)} "
        } else {
            "$ITALIC$BLUE${(it.take(maxRequestAliasLength) + "()").padEnd(maxRequestAliasLength + 2)}$END$ITALIC_END "
        }
    } else "$it() "
}
    ?: ""

private fun classLine(classLine: String?): String =
    classLine?.let { if (printCallingClassAndLine) ".($classLine)" else "" } ?: ""

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

fun String.singleLineBody(maxLength: Int = maxResponseBodyLength): String {
    var insideQuotes = false
    var key = true
    var currentLength = 0

    return if (!useColours) {
        asSequence()
            .takeWhile { _ -> currentLength++ < maxLength }
            .map { char ->
                when {
                    char.isWhitespace() && !insideQuotes -> ""
                    char == '"' -> {
                        insideQuotes = !insideQuotes
                        char
                    }

                    else -> char
                }
            }
            .joinToString("")

    } else asSequence()
        .takeWhile { _ -> currentLength++ < maxLength }
        .map { char ->
            when {
                char in arrayOf('{', '}', '[', ']') && !insideQuotes -> PURPLE + char + END
                char.isWhitespace() && !insideQuotes -> ""
                char in listOf(':', ',') -> {
                    if (!insideQuotes) {
                        key = !key
                    }
                    "$PURPLE$char$END$ITALIC"
                }

                char == '"' -> {
                    val result = when {
                        //keys
                        !insideQuotes && key -> END + BOLD + L_GRAY + '"'
                        insideQuotes && key -> L_GRAY + '"'
                        //values
                        !insideQuotes && !key -> END + ITALIC + '"'
                        else -> END + '"'
                    }

                    insideQuotes = !insideQuotes
                    result
                }

                else -> char
            }
        }
        .joinToString("") + END
}
