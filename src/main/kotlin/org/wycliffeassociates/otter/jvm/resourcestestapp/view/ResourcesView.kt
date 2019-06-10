package org.wycliffeassociates.otter.jvm.resourcestestapp.view

import org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.view.ResourceTakesView
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import org.wycliffeassociates.otter.jvm.app.ui.resources.view.ResourceListFragment
import org.wycliffeassociates.otter.jvm.app.ui.resources.viewmodel.ResourcesViewModel
import org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.viewmodel.TakesViewModel
import org.wycliffeassociates.otter.jvm.app.ui.workbook.viewmodel.WorkbookViewModel
import tornadofx.View
import tornadofx.Workspace
import tornadofx.removeFromParent
import tornadofx.vbox

class ResourcesView : View() {
    private val resourcesViewModel: ResourcesViewModel by inject()
    private val workbookViewModel: WorkbookViewModel by inject()
    private val injector: Injector by inject()
    private val collectionRepository = injector.collectionRepo
    private val workbookRepository = injector.workbookRepository

    override val root = vbox {}

    var activeFragment: Workspace = Workspace()

    init {
        setupResourcesViewModel()

        activeFragment.header.removeFromParent()
        dockResourceListFragment()
//        dockTestTakesView()
        add(activeFragment)
    }

    fun dockTakesView() {
        activeFragment.dock<ResourceTakesView>()
    }

    fun dockResourceListFragment() {
        activeFragment.dock<ResourceListFragment>()
    }

    private fun dockTestTakesView() {
//        takesViewModel.titleRecordableItem = ResourceTakesApp.titleRecordableItem
//        takesViewModel.bodyRecordableItem = ResourceTakesApp.bodyRecordableItem
        dockTakesView()
//        takesViewModel.recordableList.add(ResourceTakesApp.titleRecordableItem)
//        takesViewModel.recordableList.add(ResourceTakesApp.bodyRecordableItem)
    }

    private fun setupResourcesViewModel() {
        val targetProject = collectionRepository.getRootProjects().blockingGet().first()
        val sourceProject = collectionRepository.getSource(targetProject).blockingGet()
        val workbook = workbookRepository.get(sourceProject, targetProject)
        workbookViewModel.activeWorkbookProperty.set(workbook)
        workbookViewModel.activeChapterProperty.set(workbook.source.chapters.blockingFirst())
        workbookViewModel.activeResourceSlugProperty.set("tn")
        resourcesViewModel.loadResourceGroups()
    }
}