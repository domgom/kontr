package org.kontr.asserts

import java.lang.reflect.InvocationTargetException

/**
 * @author Domingo Gomez
 * Class that delegates to AssertJ if available in the classpath or uses local implementation of basic assertions used.
 * Shall the performance impact be too high we can decide to rely solely on the local implementation, but we must cut
 * transient dependencies to the minimum.
 */
internal inline fun <reified T> assertThat(target: T): KontrAssertion = KontrAssertion().assertThat(target)
class KontrAssertion {
    var assertJClass = try {
        Class.forName("org.assertj.core.api.Assertions")
    } catch (cnf: ClassNotFoundException) {
        null
    }
    var holder: Any? = null // holds the caller of the fluent interface "assertThat(holder) that returns holder"

    internal inline fun <reified T> assertThat(target: T): KontrAssertion =
        assertJClass?.let {
            val method = it.getMethod("assertThat", T::class.java)
            holder = method.invoke(null, target)
            this
        } ?: localAssertThat(target)

    internal inline fun <reified T> isLessThan(condition: T): KontrAssertion where T : Number =
        holder?.let {
            if (assertJClass == null) {
                return localIsLessThan(condition)
            }
            val method = it::class.java.getMethod("isLessThan", T::class.javaPrimitiveType)
            try {
                method.invoke(it, condition)

            } catch (ite: InvocationTargetException) {
                throw ite.targetException
            }
            this
        } ?: localIsLessThan(condition)

    internal inline fun <reified T> localIsLessThan(condition: T): KontrAssertion where T : Number {
        if ((holder as Number).toDouble() >= (condition as Number).toDouble()) {
            throw AssertionError("$holder is not less than $condition")
        }
        return this
    }

    internal inline fun <reified T> isBetween(lower: T, higher: T): KontrAssertion where T : Number =
        holder?.let {
            if (assertJClass == null) {
                return localIsBetween(lower, higher)
            }
            val method = it::class.java.getMethod("isBetween", T::class.java, T::class.java)
            try {
                method.invoke(it, lower, higher)
            } catch (ite: InvocationTargetException) {
                throw ite.targetException
            }
            this
        } ?: localIsBetween(lower, higher)

    internal inline fun <reified T> localIsBetween(lower: T, higher: T): KontrAssertion where T : Number {
        if (
            (holder as Number).toDouble() < (lower as Number).toDouble() ||
            (holder as Number).toDouble() > (higher as Number).toDouble()
        ) {
            throw AssertionError("$holder is not between $lower..$higher")
        }
        return this
    }

    internal inline fun <reified T> isEqualTo(condition: T): KontrAssertion where T : Number =
        holder?.let {
            if (assertJClass == null) {
                return localIsEqualTo(condition)
            }
            val method = it::class.java.getMethod("isEqualTo", T::class.javaPrimitiveType)
            try {
                method.invoke(it, condition)
            } catch (ite: InvocationTargetException) {
                throw ite.targetException
            }

            this
        } ?: localIsEqualTo(condition)

    internal inline fun <reified T> localIsEqualTo(condition: T): KontrAssertion where T : Number {
        if ((holder as Number).toDouble() != (condition as Number).toDouble()) {
            throw AssertionError("$holder is not equals to $condition")
        }
        return this
    }

    internal inline fun <reified T> localAssertThat(target: T): KontrAssertion {
        holder = target
        return this
    }
}
