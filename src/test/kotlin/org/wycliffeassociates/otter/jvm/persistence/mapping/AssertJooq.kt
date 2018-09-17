package org.wycliffeassociates.otter.jvm.persistence.mapping

import jooq.tables.pojos.LanguageEntity
import org.junit.Assert

object AssertJooq {
    fun assertLanguageEntityEquals(expected: LanguageEntity, actual: LanguageEntity) {
        Assert.assertEquals(expected.anglicized, actual.anglicized)
        Assert.assertEquals(expected.gateway, actual.gateway)
        Assert.assertEquals(expected.id, actual.id)
        Assert.assertEquals(expected.name, actual.name)
        Assert.assertEquals(expected.rtl, actual.rtl)
        Assert.assertEquals(expected.slug, actual.slug)
    }
}