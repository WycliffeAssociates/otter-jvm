package org.wycliffeassociates.otter.jvm.persistence.repositories

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert
import org.junit.Test
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Content
import org.wycliffeassociates.otter.common.data.model.Marker
import org.wycliffeassociates.otter.common.data.model.Take
import org.wycliffeassociates.otter.jvm.persistence.entities.MarkerEntity
import org.wycliffeassociates.otter.jvm.persistence.entities.TakeEntity
import org.wycliffeassociates.otter.jvm.persistence.repositories.test.MockDatabase
import java.io.File
import java.time.LocalDate

class ContentRepositoryTest {
    private val mockDatabase = MockDatabase.database()

    // UUT
    private val contentRepository = ContentRepository(mockDatabase)

    private val mockCollection: Collection = mock { on { id } doReturn 1 }

    @Test
    fun shouldCRUDContentWithoutSelectedTake() {
        val content = create()

        val retrieved = retrieveByCollection()
        Assert.assertEquals(listOf(content), retrieved)

        update(content)
        val retrievedUpdated = retrieveByCollection()
        Assert.assertEquals(listOf(content), retrievedUpdated)

        delete(content)
        val retrievedDeleted = retrieveByCollection()
        Assert.assertEquals(emptyList<Take>(), retrievedDeleted)
    }

    @Test
    fun shouldCRUDContentWithSelectedTake() {
        val take = createTake(1, File("take.wav"), 1, false,
                listOf(Marker(1, 1, "verse")))
        val content = create(selectedTake = take)

        val retrieved = retrieveByCollection()
        retrieved.forEach {
            val selectedTake = it.selectedTake
            if (selectedTake != null) {
                updateMarkerIds(selectedTake.markers, content.selectedTake?.markers ?: emptyList())
            }
        }
        Assert.assertEquals(listOf(content), retrieved)

        val newTake = createTake(1, File("newtake.wav"), 2, true,
                listOf(Marker(2, 2, "v2")))
        update(content, selectedTake = newTake)
        val retrievedUpdated = retrieveByCollection()
        retrievedUpdated.forEach {
            val selectedTake = it.selectedTake
            if (selectedTake != null) {
                updateMarkerIds(selectedTake.markers, content.selectedTake?.markers ?: emptyList())
            }
        }
        Assert.assertEquals(listOf(content), retrievedUpdated)

        delete(content)
        val retrievedDeleted = retrieveByCollection()
        Assert.assertEquals(emptyList<Take>(), retrievedDeleted)
    }

    @Test
    fun shouldGetAllContent() {
        val content = listOf(create(), create())
        val retrieved = contentRepository.getAll().blockingGet()
        Assert.assertEquals(content, retrieved)
    }

    @Test
    fun shouldGetSourcesOfContent() {
        val content = create()
        val sourceContent = listOf(
                Content(1,"v2", 2, 2, null),
                Content(1, "v3", 3, 3, null)
        )
        sourceContent.forEach {
            it.id = contentRepository.insertForCollection(it, mock { on { id } doReturn 2 }).blockingGet()
        }
        contentRepository.updateSources(content, sourceContent).blockingAwait()
        val retrievedSources = contentRepository.getSources(content).blockingGet()
        Assert.assertEquals(sourceContent, retrievedSources)
    }

    // CRUD methods
    private fun create(selectedTake: Take? = null): Content {
        val content = Content(
                1,
                "label",
                1,
                1,
                selectedTake
        )
        content.id = contentRepository.insertForCollection(content, mockCollection).blockingGet()
        return content
    }

    private fun retrieveByCollection(): List<Content> = contentRepository.getByCollection(mockCollection).blockingGet()

    private fun updateMarkerIds(retrievedMarkers: List<Marker>, originalMarkers: List<Marker>) {
        // Update the marker ids since only the take id is returned by insert
        originalMarkers.forEach { marker ->
            marker.id = retrievedMarkers.filter { it.number == marker.number }.firstOrNull()?.id ?: 0
        }
    }

    private fun update(content: Content, selectedTake: Take? = null) {
        content.sort = 2
        content.labelKey = "new-label"
        content.start = 2
        content.end = 3
        content.selectedTake = selectedTake
        val sourceContent = listOf(
                Content(1,"v2", 2, 2, null),
                Content(1, "v3", 3, 3, null)
        )
        sourceContent.forEach {
            it.id = contentRepository.insertForCollection(it, mock { on { id } doReturn 2 }).blockingGet()
        }
        contentRepository.updateSources(content, sourceContent).blockingAwait()
        contentRepository.update(content).blockingAwait()
    }

    private fun delete(content: Content) {
        contentRepository.delete(content).blockingAwait()
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