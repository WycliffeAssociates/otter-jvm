package org.wycliffeassociates.otter.jvm.persistence.repositories.mapping

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert
import org.junit.Test
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.ResourceMetadata
import org.wycliffeassociates.otter.jvm.persistence.entities.CollectionEntity

class CollectionMapperTest {
    private val mockMetadata: ResourceMetadata = mock {
        on { id } doReturn 27
    }

    // UUT
    private val collectionMapper = CollectionMapper()

    private val entity = CollectionEntity(
            0,
            null,
            null,
            "label",
            "title",
            "slug",
            0,
            mockMetadata.id
    )

    private val collection = Collection(
            0,
            "slug",
            "label",
            "title",
            mockMetadata,
            0
    )

    @Test
    fun shouldMapEntityToCollection() {
        entity.metadataFk = mockMetadata.id
        collection.resourceContainer = mockMetadata
        val result = collectionMapper.mapFromEntity(entity, mockMetadata)
        Assert.assertEquals(collection, result)
    }

    @Test
    fun shouldMapEntityNullMetadataToCollection() {
        entity.metadataFk = null
        collection.resourceContainer = null
        val result = collectionMapper.mapFromEntity(entity, null)
        Assert.assertEquals(collection, result)
    }

    @Test
    fun shouldMapCollectionToEntity() {
        collection.resourceContainer = mockMetadata
        entity.metadataFk = mockMetadata.id
        entity.parentFk = 1
        entity.sourceFk = 2
        val result = collectionMapper.mapToEntity(collection, 1, 2)
        Assert.assertEquals(entity, result)
    }

    @Test
    fun shouldMapCollectionNullParentSourceMetadataToEntity() {
        collection.resourceContainer = null
        entity.metadataFk = null
        entity.parentFk = null
        entity.sourceFk = null
        val result = collectionMapper.mapToEntity(collection)
        Assert.assertEquals(entity, result)
    }
}