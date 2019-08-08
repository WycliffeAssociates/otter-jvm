package integrationtest.rcimport

import dagger.Module
import dagger.Provides
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.ResourceMetadata
import org.wycliffeassociates.otter.common.persistence.IDirectoryProvider
import org.wycliffeassociates.resourcecontainer.ResourceContainer
import java.io.File

@Module
class TestDirectoryProviderModule {
    @Provides
    fun providesDirectoryProvider(): IDirectoryProvider = TestDirectoryProvider()
}

class TestDirectoryProvider : IDirectoryProvider {
    private val root = createTempDir("otter-test").also(File::deleteOnExit)
    private val user = root.resolve("user")
    private val app = root.resolve("app")

    private fun buildDir(base: File, vararg children: String) =
        children
            .filter(String::isNotEmpty)
            .fold(base, File::resolve)
            .apply { mkdirs() }

    override val resourceContainerDirectory: File = buildDir(app, "rc")
    override val userProfileImageDirectory: File = buildDir(app, "users", "audio")
    override val userProfileAudioDirectory: File = buildDir(app, "users", "images")
    override val audioPluginDirectory: File = buildDir(app, "plugins")

    override fun getUserDataDirectory(appendedPath: String) = buildDir(user, appendedPath)

    override fun getAppDataDirectory(appendedPath: String) = buildDir(app, appendedPath)

    override fun getProjectAudioDirectory(
        sourceMetadata: ResourceMetadata,
        book: Collection,
        chapterDirName: String
    ): File {
        return buildDir(user,
            book.resourceContainer?.creator ?: ".",
            sourceMetadata.creator,
            "${sourceMetadata.language.slug}_${sourceMetadata.identifier}",
            "v${book.resourceContainer?.version ?: "-none"}",
            book.resourceContainer?.language?.slug ?: "no_language",
            book.slug,
            chapterDirName
        )
    }

    override fun getSourceContainerDirectory(container: ResourceContainer): File {
        val dublinCore = container.manifest.dublinCore
        return buildDir(resourceContainerDirectory,
            "src",
            dublinCore.creator,
            "${dublinCore.language.identifier}_${dublinCore.identifier}",
            "v${dublinCore.version}"
        )
    }

    override fun getDerivedContainerDirectory(metadata: ResourceMetadata, source: ResourceMetadata): File {
        return buildDir(resourceContainerDirectory,
            "der",
            metadata.creator,
            source.creator,
            "${source.language.slug}_${source.identifier}",
            "v${metadata.version}",
            metadata.language.slug
        )
    }
}
