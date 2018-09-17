package org.wycliffeassociates.otter.jvm.persistence.mapping

import jooq.tables.pojos.DublinCoreEntity
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

    fun assertDublinCoreEntityEquals(expected: DublinCoreEntity, actual: DublinCoreEntity) {
        Assert.assertEquals(expected.id, actual.id)
        Assert.assertEquals(expected.conformsto, actual.conformsto)
        Assert.assertEquals(expected.creator, actual.creator)
        Assert.assertEquals(expected.description, actual.description)
        Assert.assertEquals(expected.format, actual.format)
        Assert.assertEquals(expected.identifier, actual.identifier)
        Assert.assertEquals(expected.issued, actual.issued)
        Assert.assertEquals(expected.languageFk, actual.languageFk)
        Assert.assertEquals(expected.modified, actual.modified)
        Assert.assertEquals(expected.publisher, actual.publisher)
        Assert.assertEquals(expected.subject, actual.subject)
        Assert.assertEquals(expected.title, actual.title)
        Assert.assertEquals(expected.type, actual.type)
        Assert.assertEquals(expected.version, actual.version)
        Assert.assertEquals(expected.path, actual.path)
    }
}