package org.wycliffeassociates.otter.jvm.resourcestestapp.view

import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import org.wycliffeassociates.otter.jvm.app.ui.resources.view.ResourceListFragment
import org.wycliffeassociates.otter.jvm.app.ui.resources.viewmodel.ResourcesViewModel
import tornadofx.View
import tornadofx.Workspace
import tornadofx.removeFromParent
import tornadofx.vbox

class ResourcesView : View() {
    val viewModel: ResourcesViewModel by inject()
    private val injector: Injector by inject()
    private val collectionRepository = injector.collectionRepo
    private val workbookRepository = injector.workbookRepository

    override val root = vbox {}

    var activeFragment: Workspace = Workspace()

    init {
        setupViewModel()

        activeFragment.header.removeFromParent()
        activeFragment.dock<ResourceListFragment>()
        add(activeFragment)
    }

    private fun setupViewModel() {
        val targetProject = collectionRepository.getRootProjects().blockingGet().first()
        val sourceProject = collectionRepository.getSource(targetProject).blockingGet()
        val workbook = workbookRepository.get(sourceProject, targetProject)
        viewModel.activeWorkbookProperty.set(workbook)
        viewModel.activeChapterProperty.set(workbook.source.chapters.blockingFirst())
        viewModel.activeResourceSlugProperty.set("tn")
        viewModel.loadResourceGroups()
    }
}
