package org.wycliffeassociates.otter.jvm.persistence.repositories

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert
import org.junit.Test
import org.wycliffeassociates.otter.common.data.model.Chunk
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Marker
import org.wycliffeassociates.otter.common.data.model.Take
import org.wycliffeassociates.otter.jvm.persistence.entities.MarkerEntity
import org.wycliffeassociates.otter.jvm.persistence.entities.TakeEntity
import org.wycliffeassociates.otter.jvm.persistence.repositories.test.MockDatabase
import java.io.File
import java.time.LocalDate

class ChunkRepositoryTest {
    private val mockDatabase = MockDatabase.database()

    // UUT
    private val chunkRepository = ChunkRepository(mockDatabase)

    private val mockCollection: Collection = mock { on { id } doReturn 1 }

    @Test
    fun shouldCRUDChunkWithoutSelectedTake() {
        val chunk = create()

        val retrieved = retrieveByCollection()
        Assert.assertEquals(listOf(chunk), retrieved)

        update(chunk)
        val retrievedUpdated = retrieveByCollection()
        Assert.assertEquals(listOf(chunk), retrievedUpdated)

        delete(chunk)
        val retrievedDeleted = retrieveByCollection()
        Assert.assertEquals(emptyList<Take>(), retrievedDeleted)
    }

    @Test
    fun shouldCRUDChunkWithSelectedTake() {
        val take = createTake(1, File("take.wav"), 1, false,
                listOf(Marker(1, 1, "verse")))
        val chunk = create(selectedTake = take)

        val retrieved = retrieveByCollection()
        retrieved.forEach {
            val selectedTake = it.selectedTake
            if (selectedTake != null) {
                updateMarkerIds(selectedTake.markers, chunk.selectedTake?.markers ?: emptyList())
            }
        }
        Assert.assertEquals(listOf(chunk), retrieved)

        val newTake = createTake(1, File("newtake.wav"), 2, true,
                listOf(Marker(2, 2, "v2")))
        update(chunk, selectedTake = newTake)
        val retrievedUpdated = retrieveByCollection()
        retrievedUpdated.forEach {
            val selectedTake = it.selectedTake
            if (selectedTake != null) {
                updateMarkerIds(selectedTake.markers, chunk.selectedTake?.markers ?: emptyList())
            }
        }
        Assert.assertEquals(listOf(chunk), retrievedUpdated)

        delete(chunk)
        val retrievedDeleted = retrieveByCollection()
        Assert.assertEquals(emptyList<Take>(), retrievedDeleted)
    }

    @Test
    fun shouldGetAllChunks() {
        val chunks = listOf(create(), create())
        val retrieved = chunkRepository.getAll().blockingGet()
        Assert.assertEquals(chunks, retrieved)
    }

    @Test
    fun shouldGetSourcesOfChunk() {
        val chunk = create()
        val sourceChunks = listOf(
                Chunk(1,"v2", 2, 2, null),
                Chunk(1, "v3", 3, 3, null)
        )
        sourceChunks.forEach {
            it.id = chunkRepository.insertForCollection(it, mock { on { id } doReturn 2 }).blockingGet()
        }
        chunkRepository.updateSources(chunk, sourceChunks).blockingAwait()
        val retrievedSources = chunkRepository.getSources(chunk).blockingGet()
        Assert.assertEquals(sourceChunks, retrievedSources)
    }

    // CRUD methods
    private fun create(selectedTake: Take? = null): Chunk {
        val chunk = Chunk(
                1,
                "label",
                1,
                1,
                selectedTake
        )
        chunk.id = chunkRepository.insertForCollection(chunk, mockCollection).blockingGet()
        return chunk
    }

    private fun retrieveByCollection(): List<Chunk> = chunkRepository.getByCollection(mockCollection).blockingGet()

    private fun updateMarkerIds(retrievedMarkers: List<Marker>, originalMarkers: List<Marker>) {
        // Update the marker ids since only the take id is returned by insert
        originalMarkers.forEach { marker ->
            marker.id = retrievedMarkers.filter { it.number == marker.number }.firstOrNull()?.id ?: 0
        }
    }

    private fun update(chunk: Chunk, selectedTake: Take? = null) {
        chunk.sort = 2
        chunk.labelKey = "new-label"
        chunk.start = 2
        chunk.end = 3
        chunk.selectedTake = selectedTake
        val sourceChunks = listOf(
                Chunk(1,"v2", 2, 2, null),
                Chunk(1, "v3", 3, 3, null)
        )
        sourceChunks.forEach {
            it.id = chunkRepository.insertForCollection(it, mock { on { id } doReturn 2 }).blockingGet()
        }
        chunkRepository.updateSources(chunk, sourceChunks).blockingAwait()
        chunkRepository.update(chunk).blockingAwait()
    }

    private fun delete(chunk: Chunk) {
        chunkRepository.delete(chunk).blockingAwait()
    }

    private fun createTake(contentFk: Int, path: File, number: Int, played: Boolean, markers: List<Marker>): Take {
        // Create a dummy selected take
        val take = Take(
                path.name,
                path.absoluteFile,
                number,
                LocalDate.now(),
                played,
                markers
        )
        val takeEntity = TakeEntity(
                0,
                contentFk,
                take.filename,
                take.path.absolutePath,
                take.number,
                take.timestamp.toString(),
                if (take.played) 1 else 0
        )
        takeEntity.id = mockDatabase.getTakeDao().insert(takeEntity)
        take.id = takeEntity.id
        take.markers.forEach {
            mockDatabase.getMarkerDao().insert(MarkerEntity(it.id, takeEntity.id, it.number, it.position, it.label))
        }
        return take
    }
}