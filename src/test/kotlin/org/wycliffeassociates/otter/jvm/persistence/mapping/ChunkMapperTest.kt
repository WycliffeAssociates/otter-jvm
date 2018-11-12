package org.wycliffeassociates.otter.jvm.persistence.mapping

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert
import org.junit.Test
import org.wycliffeassociates.otter.common.data.model.Chunk
import org.wycliffeassociates.otter.common.data.model.Take
import org.wycliffeassociates.otter.jvm.persistence.entities.ChunkEntity
import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.ChunkMapper

class ChunkMapperTest {
    private val mockTake: Take = mock {
        on { id } doReturn 1
    }

    // UUT
    private val chunkMapper = ChunkMapper()

    private val entity = ChunkEntity(
            0,
            0,
            "label",
            1,
            0,
            0
    )

    private val chunk = Chunk(
            0,
            "label",
            1,
            2,
            mockTake,
            0
    )

    @Test
    fun shouldMapEntityToChunk() {
        chunk.selectedTake = mockTake
        val result = chunkMapper.mapFromEntity(entity, mockTake, 2)
        Assert.assertEquals(chunk, result)
    }

    @Test
    fun shouldMapEntityToChunkNoSelectedTake() {
        chunk.selectedTake = null
        val result = chunkMapper.mapFromEntity(entity, null, 2)
        Assert.assertEquals(chunk, result)
    }

    @Test
    fun shouldMapChunkToEntity() {
        chunk.selectedTake = mockTake
        entity.selectedTakeFk = 1
        entity.collectionFk = 2
        val result = chunkMapper.mapToEntity(chunk, 2)
        Assert.assertEquals(entity, result)
    }

    @Test
    fun shouldMapChunkToEntityNoSelectedTake() {
        chunk.selectedTake = null
        entity.selectedTakeFk = null
        entity.collectionFk = 2
        val result = chunkMapper.mapToEntity(chunk, 2)
        Assert.assertEquals(entity, result)
    }
}