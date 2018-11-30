package org.wycliffeassociates.otter.jvm.persistence.repositories.mapping

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert
import org.junit.Test
import org.wycliffeassociates.otter.common.data.model.Content
import org.wycliffeassociates.otter.common.data.model.Take
import org.wycliffeassociates.otter.jvm.persistence.entities.ContentEntity

class ContentMapperTest {
    private val mockTake: Take = mock {
        on { id } doReturn 1
    }

    // UUT
    private val contentMapper = ContentMapper()

    private val entity = ContentEntity(
            0,
            0,
            "label",
            1,
            0,
            0
    )

    private val content = Content(
            0,
            "label",
            1,
            2,
            mockTake,
            0
    )

    @Test
    fun shouldMapEntityToContent() {
        content.selectedTake = mockTake
        val result = contentMapper.mapFromEntity(entity, mockTake, 2)
        Assert.assertEquals(content, result)
    }

    @Test
    fun shouldMapEntityToContentNoSelectedTake() {
        content.selectedTake = null
        val result = contentMapper.mapFromEntity(entity, null, 2)
        Assert.assertEquals(content, result)
    }

    @Test
    fun shouldMapContentToEntity() {
        content.selectedTake = mockTake
        entity.selectedTakeFk = 1
        entity.collectionFk = 2
        val result = contentMapper.mapToEntity(content, 2)
        Assert.assertEquals(entity, result)
    }

    @Test
    fun shouldMapContentToEntityNoSelectedTake() {
        content.selectedTake = null
        entity.selectedTakeFk = null
        entity.collectionFk = 2
        val result = contentMapper.mapToEntity(content, 2)
        Assert.assertEquals(entity, result)
    }
}