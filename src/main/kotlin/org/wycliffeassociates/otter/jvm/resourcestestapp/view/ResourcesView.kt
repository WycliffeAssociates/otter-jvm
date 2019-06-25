package org.wycliffeassociates.otter.jvm.resourcestestapp.view

import javafx.scene.layout.Priority
import org.wycliffeassociates.otter.common.data.workbook.Workbook
import org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.view.ResourceTakesView
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import org.wycliffeassociates.otter.jvm.app.ui.menu.view.MainMenu
import org.wycliffeassociates.otter.jvm.app.ui.resources.view.ResourceListFragment
import org.wycliffeassociates.otter.jvm.app.ui.resources.viewmodel.ResourcesViewModel
import org.wycliffeassociates.otter.jvm.app.ui.workbook.viewmodel.WorkbookViewModel
import tornadofx.*
import java.io.File

class ResourcesView : View() {
    private val resourcesViewModel: ResourcesViewModel by inject()
    private val workbookViewModel: WorkbookViewModel by inject()
    private val injector: Injector by inject()
    private val directoryProvider = injector.directoryProvider
    private val collectionRepository = injector.collectionRepo
    private val workbookRepository = injector.workbookRepository

    override val root = vbox {}

    var activeFragment: Workspace = Workspace()

    init {
        setupResourcesViewModel()

//        activeFragment.header.removeFromParent()

        activeFragment.root.apply {
            vgrow = Priority.ALWAYS
        }

        activeFragment.add(MainMenu())
        dockResourceListFragment()
        add(activeFragment)
    }

    fun dockTakesView() {
        activeFragment.dock<ResourceTakesView>()
    }

    fun dockResourceListFragment() {
        activeFragment.dock<ResourceListFragment>()
    }

    private fun setupResourcesViewModel() {
        val targetProject = collectionRepository.getRootProjects().blockingGet().first()
        val sourceProject = collectionRepository.getSource(targetProject).blockingGet()
        val workbook = workbookRepository.get(sourceProject, targetProject)
        workbookViewModel.activeWorkbookProperty.set(workbook)
        workbookViewModel.activeChapterProperty.set(workbook.source.chapters.blockingFirst())
        workbookViewModel.activeResourceSlugProperty.set("tn")
        workbookViewModel.activeProjectAudioDirectoryProperty.set(getTestProjectAudioDirectory(workbook))
        resourcesViewModel.loadResourceGroups()
    }

    private fun getTestProjectAudioDirectory(workbook: Workbook): File {
        val path = directoryProvider.getUserDataDirectory(
            "testAudioPath\\" +
                    "${workbook.targetLanguageSlug}\\" +
                    "${workbook.target.slug}\\"
        )
        path.mkdirs()
        return path
    }
}