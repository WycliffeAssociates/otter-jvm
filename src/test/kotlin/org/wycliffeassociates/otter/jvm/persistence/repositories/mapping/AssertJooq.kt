package org.wycliffeassociates.otter.jvm.persistence.repositories.mapping

import org.junit.Assert

object AssertJooq {
    fun <T: Any> assertEntityEquals(expected: T, actual: T) {
        expected.javaClass.declaredFields.forEach {
            it.isAccessible = true
            val expectedValue = it.get(expected)
            val actualValue = it.get(actual)
            println("${it.name} [Expected: $expectedValue, Actual: $actualValue]")
            Assert.assertEquals(expectedValue, actualValue)
            it.isAccessible = false
        }
    }
}