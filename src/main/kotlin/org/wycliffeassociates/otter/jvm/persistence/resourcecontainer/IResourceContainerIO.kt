package org.wycliffeassociates.otter.jvm.persistence.resourcecontainer

import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.resourcecontainer.ResourceContainer
import java.io.File

interface IResourceContainerIO {
    fun load(dir: File): ResourceContainer
    fun createForNewProject(source: Collection, targetLanguage: Language): ResourceContainer
}