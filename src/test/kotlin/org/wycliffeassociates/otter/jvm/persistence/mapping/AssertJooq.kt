package org.wycliffeassociates.otter.jvm.persistence.mapping

import org.junit.Assert

object AssertJooq {
    fun <T: Any> assertEntityEquals(expected: T, actual: T) {
        expected.javaClass.declaredFields.forEach {
            it.isAccessible = true
            val expectedValue = it.get(expected)
            val actualValue = it.get(actual)
            println("Field: ${it.name}, Expected: $expectedValue, Actual: $actualValue")
            Assert.assertEquals(expectedValue, actualValue)
        }
    }
}