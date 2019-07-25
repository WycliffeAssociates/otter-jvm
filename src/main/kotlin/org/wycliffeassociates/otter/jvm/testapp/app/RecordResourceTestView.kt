package org.wycliffeassociates.otter.jvm.testapp.app

import javafx.scene.layout.Priority
import org.wycliffeassociates.otter.common.data.workbook.Workbook
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import org.wycliffeassociates.otter.jvm.app.ui.mainscreen.view.MainScreenStyles
import org.wycliffeassociates.otter.jvm.app.ui.menu.view.MainMenu
import org.wycliffeassociates.otter.jvm.app.ui.resources.viewmodel.ResourceListViewModel
import org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.view.ResourceTabPaneFragment
import org.wycliffeassociates.otter.jvm.app.ui.workbook.viewmodel.WorkbookViewModel
import tornadofx.*
import java.io.File

class RecordResourceTestView: View() {

    private val injector: Injector by inject()
    private val resourcesViewModel: ResourceListViewModel by inject()
    private val workbookViewModel: WorkbookViewModel by inject()

    private val directoryProvider = injector.directoryProvider
    private val collectionRepository = injector.collectionRepo
    val workbookRepository = injector.workbookRepository

    var activeFragment: Workspace = Workspace()

    override val root = vbox {}

    init {
        importStylesheet<MainScreenStyles>()

        activeFragment.root.apply {
            vgrow = Priority.ALWAYS
            addClass(MainScreenStyles.main)
        }

        activeFragment.add(MainMenu())

        add(activeFragment)

        setupResourcesViewModel()
    }

    private fun setupResourcesViewModel() {
        val targetProject = collectionRepository.getRootProjects().blockingGet().first()
        val sourceProject = collectionRepository.getSource(targetProject).blockingGet()
        val workbook = workbookRepository.get(sourceProject, targetProject)
        val chapter = workbook.source.chapters.blockingFirst()
        val chunk = chapter.chunks.blockingFirst()

        workbookViewModel.activeResourceSlugProperty.set("tn")
//        workbookViewModel.activeResourceSlugProperty.set("ulb")
        workbookViewModel.activeProjectAudioDirectoryProperty.set(getTestProjectAudioDirectory(workbook))

        workbookViewModel.activeWorkbookProperty.set(workbook)
        workbookViewModel.activeChapterProperty.set(chapter)

//        resourcesViewModel.loadResourceGroups(chapter)

        resourcesViewModel.navigateToTakesPage(chunk, chunk.resources[0].resources.blockingFirst())
        activeFragment.dock<ResourceTabPaneFragment>()
    }

    private fun getTestProjectAudioDirectory(workbook: Workbook): File {
        val path = directoryProvider.getUserDataDirectory(
            "testAudioPath\\" +
                    "${workbook.target.language.slug}\\" +
                    "${workbook.target.slug}\\"
        )
        path.mkdirs()
        return path
    }
}
