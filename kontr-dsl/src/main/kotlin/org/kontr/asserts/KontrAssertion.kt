package org.kontr.asserts

/**
 * @author Domingo Gomez
 */
internal fun <T> assertThat(value: T): KontrAssertion where T : Number = KontrAssertion(value)
class KontrAssertion(
    private val valueHolder: Number  // holds the argument of the fluent interface "assertThat(argument)"
) {
    internal fun <T> isLessThan(condition: T): KontrAssertion where T : Number =
        if (valueHolder.toDouble() >= condition.toDouble()) {
            throw AssertionError("\nExpecting actual:\n  $valueHolder\nto be less than:\n  $condition ")
        } else this

    internal fun <T> isBetween(lower: T, higher: T): KontrAssertion where T : Number =
        if (valueHolder.toDouble() < lower.toDouble() ||
            valueHolder.toDouble() > higher.toDouble()
        ) {
            throw AssertionError("\nExpecting actual:\n  $valueHolder\nto be between:\n  [$lower, $higher]\n")
        } else this

    internal fun <T> isEqualTo(condition: T): KontrAssertion where T : Number =
        if (valueHolder.toDouble() != condition.toDouble()) {
            throw AssertionError("\nexpected: $condition\n but was: $valueHolder")
        } else this
}
