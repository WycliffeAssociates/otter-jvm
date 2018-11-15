package org.wycliffeassociates.otter.jvm.persistence.resourcecontainer

import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.common.persistence.IDirectoryProvider
import org.wycliffeassociates.resourcecontainer.ResourceContainer
import org.wycliffeassociates.resourcecontainer.entity.Checking
import org.wycliffeassociates.resourcecontainer.entity.Manifest
import org.wycliffeassociates.resourcecontainer.entity.dublincore
import org.wycliffeassociates.resourcecontainer.entity.language
import java.io.File
import java.lang.NullPointerException
import java.time.LocalDate

class ResourceContainerIO(
        private val directoryProvider: IDirectoryProvider
) : IResourceContainerIO {
    override fun load(dir: File): ResourceContainer = ResourceContainer.load(dir)

    override fun createForNewProject(source: Collection, targetLanguage: Language): ResourceContainer {
        val metadata = source.resourceContainer
        metadata ?: throw NullPointerException("Source has no resource metadata")

        val slug = "${targetLanguage.slug}_${metadata.identifier}"
        val directory = directoryProvider.resourceContainerDirectory.resolve(slug)
        val container = ResourceContainer.create(directory) {
            // Set up the manifest
            manifest = Manifest(
                    dublincore {
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
                    },
                    listOf(),
                    Checking()
            )
        }
        container.write()
        return container
    }

}