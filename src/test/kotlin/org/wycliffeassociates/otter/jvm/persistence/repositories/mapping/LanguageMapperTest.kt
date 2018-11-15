package org.wycliffeassociates.otter.jvm.persistence.repositories.mapping

import org.junit.Assert
import org.junit.Test
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.jvm.persistence.entities.LanguageEntity

class LanguageMapperTest {
    // unit under test
    private val languageMapper = LanguageMapper()

    private val entity = LanguageEntity(
            0,
            "en",
            "English",
            "English",
            "ltr",
            1
    )

    private val language = Language(
            "en",
            "English",
            "English",
            "ltr",
            true,
            0
    )

    @Test
    fun shouldMapGatewayEntityToLanguage() {
        entity.gateway = 1
        language.isGateway = true
        val result = languageMapper.mapFromEntity(entity)
        Assert.assertEquals(language, result)
    }

    @Test
    fun shouldMapGatewayLanguageToEntity() {
        language.isGateway = true
        entity.gateway = 1
        val result = languageMapper.mapToEntity(language)
        Assert.assertEquals(entity, result)
    }

    @Test
    fun shouldMapNonGatewayEntityToLanguage() {
        entity.gateway = 0
        language.isGateway = false
        val result = languageMapper.mapFromEntity(entity)
        Assert.assertEquals(language, result)
    }

    @Test
    fun shouldMapNonGatewayLanguageToEntity() {
        language.isGateway = false
        entity.gateway = 0
        val result = languageMapper.mapToEntity(language)
        Assert.assertEquals(entity, result)
    }

}
