package org.wycliffeassociates.otter.jvm.persistence.resourcecontainer

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert
import org.junit.Test
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.common.data.model.ResourceMetadata
import org.wycliffeassociates.otter.common.persistence.IDirectoryProvider
import org.wycliffeassociates.resourcecontainer.entity.Checking
import org.wycliffeassociates.resourcecontainer.entity.Project
import org.wycliffeassociates.resourcecontainer.entity.dublincore
import org.wycliffeassociates.resourcecontainer.entity.language
import java.io.File
import java.time.LocalDate

class ResourceContainerIOTest {
    private val mockDirectoryProvider: IDirectoryProvider = mock {
        on { resourceContainerDirectory } doReturn File(".")
    }

    // UUT
    private val rcIO = ResourceContainerIO(mockDirectoryProvider)

    /* not testing load, since that would be a repeat of RC lib tests */

    @Test
    fun shouldCreateNewProject() {
        val source = Collection(
                1,
                "gen",
                "project",
                "Genesis",
                ResourceMetadata(
                        "rc0.2",
                        "Bible Create",
                        "The ULB translation.",
                        "text/usfm",
                        "ulb",
                        LocalDate.of(2000, 1, 1),
                        Language(
                                "en",
                                "English",
                                "English",
                                "ltr",
                                true
                        ),
                        LocalDate.of(2018, 3, 2),
                        "Bible Publisher",
                        "Bible",
                        "book",
                        "Unlocked Literal Bible",
                        "3",
                        File(".")
                )
        )
        val targetLanguage = Language(
                "ar",
                "العربية",
                "Arabic",
                "rtl",
                true
        )
        val expectedDublinCore = dublincore {
            conformsTo = "0.2"
            identifier = source.resourceContainer!!.identifier
            issued = LocalDate.now().toString()
            modified = LocalDate.now().toString()
            language = language {
                identifier = targetLanguage.slug
                direction = targetLanguage.direction
                title = targetLanguage.name
            }
            format = "text/usfm"
            subject = source.resourceContainer!!.subject
            type = "book"
            title = source.resourceContainer!!.title
        }
        val expectedChecking = Checking()

        val expectedManifestPath = mockDirectoryProvider
                .resourceContainerDirectory
                .resolve("${targetLanguage.slug}_${source.resourceContainer!!.identifier}")

        val container = rcIO.createForNewProject(source, targetLanguage)

        Assert.assertEquals(expectedDublinCore, container.manifest.dublinCore)
        Assert.assertEquals(emptyList<Project>(), container.manifest.projects)
        Assert.assertEquals(expectedChecking, container.manifest.checking)
        Assert.assertTrue(expectedManifestPath.exists())
        expectedManifestPath.deleteRecursively()
    }

    @Test(expected = NullPointerException::class)
    fun shouldThrowNPEIfNoSourceMetadata() {
        val source = Collection(
                1,
                "gen",
                "project",
                "Genesis",
                null
        )
        val targetLanguage: Language = mock()

        rcIO.createForNewProject(source, targetLanguage)
    }
}