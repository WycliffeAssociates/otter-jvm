package org.wycliffeassociates.otter.jvm.persistence.repositories

import org.junit.Assert
import org.junit.Test
import org.wycliffeassociates.otter.common.data.audioplugin.AudioPluginData
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.common.data.model.ResourceMetadata
import org.wycliffeassociates.otter.jvm.persistence.entities.LanguageEntity
import org.wycliffeassociates.otter.jvm.persistence.repositories.test.MockDatabase
import java.io.File
import java.time.LocalDate

class ResourceMetadataRepositoryTest {
    private val mockDatabase = MockDatabase.database()

    // UUT
    private val metadataRepository = ResourceMetadataRepository(mockDatabase)

    @Test
    fun shouldCRUDMetadata() {
        val metadata = create()

        val retrieved = retrieveAll()
        Assert.assertEquals(listOf(metadata), retrieved)

        update(metadata)
        val retrievedUpdated = retrieveAll()
        Assert.assertEquals(listOf(metadata), retrievedUpdated)

        delete(metadata)
        val retrievedDeleted = retrieveAll()
        Assert.assertEquals(emptyList<AudioPluginData>(), retrievedDeleted)
    }

    @Test
    fun shouldAddGetRemoveLink() {
        val metadata1 = create()
        val metadata2 = create()
        metadataRepository.addLink(metadata1, metadata2).blockingAwait()
        val retrievedLinks1 = metadataRepository.getLinked(metadata1).blockingGet()
        val retrievedLinks2 = metadataRepository.getLinked(metadata2).blockingGet()
        Assert.assertEquals(listOf(metadata2), retrievedLinks1)
        Assert.assertEquals(listOf(metadata1), retrievedLinks2)
        metadataRepository.removeLink(metadata1, metadata2).blockingAwait()
        val deletedLinks1 = metadataRepository.getLinked(metadata1).blockingGet()
        val deletedLinks2 = metadataRepository.getLinked(metadata2).blockingGet()
        Assert.assertEquals(emptyList<ResourceMetadata>(), deletedLinks1)
        Assert.assertEquals(emptyList<ResourceMetadata>(), deletedLinks2)
    }

    // CRUD methods
    private fun create(): ResourceMetadata
    {
        val metadata = ResourceMetadata(
                "conformsTo",
                "creator",
                "description",
                "format",
                "identifier",
                LocalDate.now(),
                createLanguage(),
                LocalDate.now(),
                "publisher",
                "subject",
                "type",
                "title",
                "version",
                File(".").absoluteFile
        )
        metadata.id = metadataRepository.insert(metadata).blockingGet()
        return metadata
    }

    private fun retrieveAll(): List<ResourceMetadata> = metadataRepository
            .getAll()
            .blockingGet()

    private fun update(metadata: ResourceMetadata) {
        metadata.conformsTo = "rc0.2"
        metadata.creator = "WA"
        metadata.description = "Unlocked Literal Bible"
        metadata.format = "book"
        metadata.identifier = "ulb"
        metadata.issued = LocalDate.of(2018, 10, 19)
        metadata.language = createLanguage("ar")
        metadata.publisher = "Wycliffe Associates"
        metadata.subject = "Bible"
        metadata.title = "ULB"
        metadata.version = "1.0.0"
        File("./new").absoluteFile
        metadataRepository.update(metadata).blockingAwait()
    }

    private fun delete(metadata: ResourceMetadata) {
        metadataRepository.delete(metadata).blockingAwait()
    }

    private fun createLanguage(slug: String = "slug"): Language {
        val language = Language(
                slug,
                "name",
                "anglicizedName",
                "directory",
                true
        )
        val entity = LanguageEntity(
                0,
                language.slug,
                language.name,
                language.anglicizedName,
                language.direction,
                if (language.isGateway) 1 else 0
        )
        language.id = mockDatabase.getLanguageDao().insert(entity)
        return language
    }
}