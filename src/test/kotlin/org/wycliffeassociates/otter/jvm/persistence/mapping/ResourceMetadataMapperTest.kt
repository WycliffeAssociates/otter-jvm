package org.wycliffeassociates.otter.jvm.persistence.mapping

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert
import org.junit.Test
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.common.data.model.ResourceMetadata
import org.wycliffeassociates.otter.jvm.persistence.entities.ResourceMetadataEntity
import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.ResourceMetadataMapper
import java.io.File
import java.time.LocalDate

class ResourceMetadataMapperTest {
    private val mockLanguage: Language = mock {
        on { id } doReturn 1800
    }

    // UUT
    private val metadataMapper = ResourceMetadataMapper()

    private val entity = ResourceMetadataEntity(
            0,
            "rc0.2",
            "creator",
            "description",
            "format",
            "identifier",
            "2000-01-01",
            1800,
            "2018-02-04",
            "publisher",
            "subject",
            "type",
            "title",
            "version",
            "/path/to/rc"
    )

    private val metadata = ResourceMetadata(
            "rc0.2",
            "creator",
            "description",
            "format",
            "identifier",
            LocalDate.of(2000, 1, 1),
            mockLanguage,
            LocalDate.of(2018, 2, 4),
            "publisher",
            "subject",
            "type",
            "title",
            "version",
            File("/path/to/rc")
    )

    @Test
    fun shouldMapEntityToMetadata() {
        val result = metadataMapper.mapToEntity(metadata)
        Assert.assertEquals(entity, result)
    }

    @Test
    fun shouldMapMetadataToEntity() {
        val result = metadataMapper.mapFromEntity(entity, mockLanguage)
        Assert.assertEquals(metadata, result)
    }
}