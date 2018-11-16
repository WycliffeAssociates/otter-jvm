package org.wycliffeassociates.otter.jvm.persistence.repositories.test

import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.spy
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.jvm.persistence.resourcecontainer.IResourceContainerIO
import org.wycliffeassociates.resourcecontainer.ResourceContainer
import org.wycliffeassociates.resourcecontainer.entity.Checking
import org.wycliffeassociates.resourcecontainer.entity.Manifest
import org.wycliffeassociates.resourcecontainer.entity.dublincore
import org.wycliffeassociates.resourcecontainer.entity.language
import java.io.File
import java.time.LocalDate

class MockResourceContainerIO : IResourceContainerIO {
    var container: ResourceContainer = spy(ResourceContainer.create(File("./rc")) {
        // Set up the manifest
        manifest = Manifest(
                dublincore {
                },
                listOf(),
                Checking()
        )
    })
    init {
        // Stub the write function
        doNothing().`when`(container).write()
        doNothing().`when`(container).writeManifest()
    }
    override fun load(dir: File): ResourceContainer {
        return container
    }

    override fun createForNewProject(source: Collection, targetLanguage: Language): ResourceContainer {
        val metadata = source.resourceContainer
        metadata ?: throw NullPointerException("Source has no resource metadata")
        container.manifest.dublinCore.apply {
            identifier = metadata.identifier
            issued = LocalDate.now().toString()
            modified = LocalDate.now().toString()
            language = language {
                identifier = targetLanguage.slug
                direction = targetLanguage.direction
                title = targetLanguage.name
            }
            format = "text/usfm"
            subject = metadata.subject
            type = "book"
            title = metadata.title
        }
        return container
    }
}