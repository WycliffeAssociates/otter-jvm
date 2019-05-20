package org.wycliffeassociates.otter.jvm.resourcestestapp.view

import org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.view.ResourceTakesView
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import org.wycliffeassociates.otter.jvm.app.ui.resources.view.ResourceListFragment
import org.wycliffeassociates.otter.jvm.app.ui.resources.viewmodel.ResourcesViewModel
import tornadofx.View
import tornadofx.Workspace
import tornadofx.removeFromParent
import tornadofx.vbox

class ResourcesView : View() {
    val resourcesViewModel: ResourcesViewModel by inject()
    private val injector: Injector by inject()
    private val collectionRepository = injector.collectionRepo
    private val workbookRepository = injector.workbookRepository

    override val root = vbox {}

    var activeFragment: Workspace = Workspace()

    init {
        setupResourcesViewModel()

        activeFragment.header.removeFromParent()
        activeFragment.dock<ResourceListFragment>()
//        activeFragment.dock<ResourceTakesView>()
        add(activeFragment)
    }

    fun dockTakesView() {
        activeFragment.dock<ResourceTakesView>()
    }

    private fun setupResourcesViewModel() {
        val targetProject = collectionRepository.getRootProjects().blockingGet().first()
        val sourceProject = collectionRepository.getSource(targetProject).blockingGet()
        val workbook = workbookRepository.get(sourceProject, targetProject)
        resourcesViewModel.activeWorkbookProperty.set(workbook)
        resourcesViewModel.activeChapterProperty.set(workbook.source.chapters.blockingFirst())
        resourcesViewModel.activeResourceSlugProperty.set("tn")
        resourcesViewModel.loadResourceGroups()
    }
}