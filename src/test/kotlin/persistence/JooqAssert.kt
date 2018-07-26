package persistence

import org.junit.Assert
import persistence.tables.pojos.LanguageEntity

object JooqAssert {

    fun assertLanguageEqual(expected: LanguageEntity, result: LanguageEntity){
        Assert.assertEquals(expected.id, result.id)
        Assert.assertEquals(expected.name, result.name)
        Assert.assertEquals(expected.slug, expected.slug)
        Assert.assertEquals(expected.isgateway, expected.isgateway)
        Assert.assertEquals(expected.anglicizedname, expected.anglicizedname)
    }
}