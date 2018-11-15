package org.wycliffeassociates.otter.jvm.persistence.repositories

import com.nhaarman.mockitokotlin2.*
import org.junit.Assert
import org.junit.Test
import org.wycliffeassociates.otter.common.data.model.*
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.persistence.IDirectoryProvider
import org.wycliffeassociates.otter.jvm.persistence.database.queries.DeriveProjectQuery
import org.wycliffeassociates.otter.jvm.persistence.entities.LanguageEntity
import org.wycliffeassociates.otter.jvm.persistence.entities.ResourceMetadataEntity
import org.wycliffeassociates.otter.jvm.persistence.repositories.test.MockDatabase
import org.wycliffeassociates.otter.jvm.persistence.resourcecontainer.IResourceContainerIO
import java.io.File
import java.lang.RuntimeException
import java.time.LocalDate

class CollectionRepositoryTest {
    private val mockDatabase = MockDatabase.database()
    private val mockDirectoryProvider: IDirectoryProvider = mock {

    }
    private val mockRcIO: IResourceContainerIO = mock {

    }
    private val mockDeriveProjectQuery: DeriveProjectQuery = mock {

    }

    // UUT
    private val collectionRepository = CollectionRepository(
            mockDatabase,
            mockDirectoryProvider,
            rcIO = mockRcIO,
            deriveProjectQuery = mockDeriveProjectQuery
    )

    @Test
    fun shouldCRUDCollection() {
        val collection = create()

        val retrieved = retrieveAll()
        Assert.assertEquals(listOf(collection), retrieved)

        update(collection)
        val retrievedUpdated = retrieveAll()
        Assert.assertEquals(listOf(collection), retrievedUpdated)

        delete(collection)
        val retrievedDeleted = retrieveAll()
        Assert.assertEquals(emptyList<Take>(), retrievedDeleted)
    }

    @Test
    fun shouldHandleUpdateOfNonExistentCollection() {
        val collection: Collection = mock { on { id } doReturn 0 }
        whenever(mockDatabase.getCollectionDao().fetchById(any(), anyOrNull())).thenThrow(RuntimeException())
        try {
            collectionRepository.update(collection).blockingAwait()
        } catch (e: RuntimeException) {
            Assert.fail("Did not handle DAO exception")
        }
    }

    @Test
    fun shouldUpdateParentGetChildren() {
        val parent = create()
        val collection = create()
        collectionRepository.updateParent(collection, parent).blockingAwait()
        val retrievedChildren = collectionRepository.getChildren(parent).blockingGet()
        Assert.assertEquals(listOf(collection), retrievedChildren)
    }

    @Test
    fun shouldHandleUpdateParentOfNonExistentCollection() {
        val parent = create()
        val collection: Collection = mock { on { id } doReturn  0 }
        try {
            collectionRepository.updateParent(collection, parent).blockingAwait()
        } catch (e: RuntimeException) {
            Assert.fail("Did not handle DAO exception")
        }
    }

    @Test
    fun shouldUpdateSource() {
        val source = create()
        val collection = create()
        collectionRepository.updateSource(collection, source).blockingAwait()
        val sourceId = mockDatabase.getCollectionDao().fetchById(collection.id).sourceFk
        Assert.assertEquals(source.id, sourceId)
    }

    @Test
    fun shouldHandleUpdateSourceOfNonExistentCollection() {
        val source = create()
        val collection: Collection = mock { on { id } doReturn  0 }
        try {
            collectionRepository.updateSource(collection, source).blockingAwait()
        } catch (e: RuntimeException) {
            Assert.fail("Did not handle DAO exception")
        }
    }

    @Test
    fun shouldGetAllCollections() {
        val collections = listOf(create(), create())
        val retrieved = collectionRepository.getAll().blockingGet()

        Assert.assertEquals(collections, retrieved)
    }

    @Test
    fun shouldGetBySlugAndContainer() {
        val collection = create()
        val retrieved = collectionRepository.getBySlugAndContainer(collection.slug, collection.resourceContainer!!)
                .blockingGet()
        Assert.assertEquals(collection, retrieved)
    }

    @Test
    fun shouldHandleGetByInvalidSlugAndContainer() {
        try {
            collectionRepository.getBySlugAndContainer("nonexistent-slug", createMetadata()).blockingGet()
        } catch (e: RuntimeException) {
            Assert.fail("Threw runtime exception")
        }
    }

    // CRUD methods
    private fun create(): Collection {
        val collection = Collection(
                1,
                "slug",
                "label",
                "title",
                createMetadata()
        )
        collection.id = collectionRepository.insert(collection).blockingGet()
        return collection
    }

    private fun retrieveAll(): List<Collection> = collectionRepository.getAll().blockingGet()

    private fun update(
            collection: Collection,
            parent: Collection? = null,
            source: Collection? = null
    ) {
        collection.sort = 2
        collection.slug = "new-slug"
        collection.labelKey = "new-label"
        collection.titleKey = "new-title"
        collection.resourceContainer = createMetadata()
        collectionRepository.update(collection).blockingAwait()
        if (parent != null) collectionRepository.updateParent(collection, parent)
        if (source != null) collectionRepository.updateSource(collection, source)
    }

    private fun delete(collection: Collection) {
        collectionRepository.delete(collection).blockingAwait()
    }

    private fun createMetadata(): ResourceMetadata {
        // Create a dummy selected take
        val metadata = ResourceMetadata(
                "rc0.2",
                "creator",
                "description",
                "format",
                "identifier",
                LocalDate.of(2000, 1, 1),
                createLanguage(),
                LocalDate.of(2018, 2, 4),
                "publisher",
                "subject",
                "type",
                "title",
                "version",
                File("/path/to/rc")
        )
        val metadataEntity = ResourceMetadataEntity(
                0,
                metadata.conformsTo,
                metadata.creator,
                metadata.description,
                metadata.format,
                metadata.identifier,
                metadata.issued.toString(),
                metadata.language.id,
                metadata.modified.toString(),
                metadata.publisher,
                metadata.subject,
                metadata.type,
                metadata.title,
                metadata.version,
                metadata.path.toURI().path
        )
        metadataEntity.id = mockDatabase.getResourceMetadataDao().insert(metadataEntity)
        metadata.id = metadataEntity.id
        return metadata
    }

    private fun createLanguage(): Language {
        val language = Language(
                "en",
                "English",
                "English",
                "ltr",
                true
        )
        val languageEntity = LanguageEntity(
                0,
                language.slug,
                language.name,
                language.anglicizedName,
                language.direction,
                if (language.isGateway) 1 else 0
        )
        languageEntity.id = mockDatabase.getLanguageDao().insert(languageEntity)
        language.id = languageEntity.id
        return language
    }
}