package org.wycliffeassociates.otter.jvm.resourcestestapp.view

import com.jfoenix.controls.JFXButton
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.scene.layout.Priority
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import org.wycliffeassociates.otter.jvm.app.ui.resources.view.ResourceListFragment
import org.wycliffeassociates.otter.jvm.app.ui.resources.viewmodel.ResourcesViewModel
import tornadofx.*

class ResourcesView : View() {
    val viewModel: ResourcesViewModel by inject()
    private val injector: Injector by inject()
    private val collectionRepository = injector.collectionRepo
    private val workbookRepository = injector.workbookRepository

    override val root = vbox {}

    var activeWorkspace: Workspace = Workspace()

    init {
        setupViewModel()

        activeWorkspace.header.removeFromParent()
        activeWorkspace.root.apply {
            vgrow = Priority.ALWAYS
        }
        dock()
        add(activeWorkspace)
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

    private var docked = false

    private fun undock() {
        activeWorkspace.dock(DummyView())
        docked = false
    }

    private fun dock() {
        activeWorkspace.dock<ResourceListFragment>()
        docked = true
    }

    fun dockOrUndock() {
        if (docked) undock() else dock()
    }

    inner class DummyView: View() {
        override val root = vbox {
            add(
                JFXButton("Back", MaterialIconView(MaterialIcon.ARROW_BACK, "25px")).apply {
                    action { dockOrUndock() }
                }
            )
        }
    }
}