package integrationtest.rcimport

import org.junit.Assert
import org.junit.Test
import org.wycliffeassociates.otter.common.domain.languages.ImportLanguages
import org.wycliffeassociates.otter.common.domain.resourcecontainer.ImportResourceContainer
import org.wycliffeassociates.otter.common.domain.resourcecontainer.ImportResult
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import org.wycliffeassociates.otter.jvm.persistence.database.AppDatabase
import java.io.File

class TestRcImport {

    @Test
    fun ulb() {
        val env = ImportEnvironment()
        env.import("en_ulb.zip")

        env.assertRowCounts(
            collectionCount = 1256,
            contentCount = 32292,
            linkCount = 0
        )
    }

    @Test
    fun ulbTn() {
        val env = ImportEnvironment()
        env.import("en_ulb.zip")
        env.import("en_tn.zip")

        env.assertRowCounts(
            collectionCount = 1256,
            contentCount = 189873,
            linkCount = 157573
        )
    }

    @Test
    fun obs() {
        val env = ImportEnvironment()
        env.import("en_obs-master.zip")

        env.assertRowCounts(
            collectionCount = 57,
            contentCount = 1313,
            linkCount = 0
        )
    }

    @Test
    fun obsTn() {
        val env = ImportEnvironment()
        env.import("en_obs-master.zip")
        env.import("en_obs-tn-master.zip")

        env.assertRowCounts(
            collectionCount = 57,
            contentCount = 5923,
            linkCount = 3138
        )
    }
}

private class ImportEnvironment {
    val persistenceComponent: TestPersistenceComponent =
        DaggerTestPersistenceComponent
            .builder()
            .testDirectoryProviderModule(TestDirectoryProviderModule())
            .build()
    val db: AppDatabase = persistenceComponent.injectDatabase()
    val injector = Injector(persistenceComponent = persistenceComponent)

    init {
        setUpDatabase()
    }

    val importer
        get() = ImportResourceContainer(
            injector.resourceContainerRepository,
            injector.directoryProvider,
            injector.zipEntryTreeBuilder
        )

    fun import(rcFile: String) {
        val result = importer.import(rcResourceFile(rcFile)).blockingGet()
        Assert.assertEquals(ImportResult.SUCCESS, result)
    }

    fun assertRowCounts(
        collectionCount: Int,
        contentCount: Int,
        linkCount: Int
    ) {
        Assert.assertEquals(
            Counts(
                collections = collectionCount,
                contents = contentCount,
                links = linkCount
            ),
            Counts(
                collections = db.collectionDao.fetchAll().count(),
                contents = db.contentDao.fetchAll().count(),
                links = db.resourceLinkDao.fetchAll().count()
            )
        )
    }

    private fun setUpDatabase() {
        val langNames = ClassLoader.getSystemResourceAsStream("content/langnames.json")!!
        ImportLanguages(langNames, injector.languageRepo)
            .import()
            .onErrorComplete()
            .blockingAwait()
    }

    private fun rcResourceFile(rcFile: String) =
        File(
            TestRcImport::class.java.classLoader
                .getResource("resource-containers/$rcFile")!!
                .toURI()
                .path
        )

    private data class Counts(
        val collections: Int,
        val contents: Int,
        val links: Int
    )
}
