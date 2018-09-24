package org.wycliffeassociates.otter.jvm.app.ui.projectcreation.view

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.property.ObjectProperty
import javafx.event.ActionEvent
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.layout.Priority
import org.wycliffeassociates.otter.jvm.app.ui.imageLoader
import org.wycliffeassociates.otter.jvm.app.ui.projectcreation.view.fragments.SelectBook
import org.wycliffeassociates.otter.jvm.app.ui.projectcreation.view.fragments.SelectLanguage
import org.wycliffeassociates.otter.jvm.app.ui.projectcreation.view.fragments.SelectResource
import org.wycliffeassociates.otter.jvm.app.ui.projectcreation.view.fragments.SelectTestament
import org.wycliffeassociates.otter.jvm.app.ui.projectcreation.viewmodel.ProjectCreationViewModel
import org.wycliffeassociates.otter.jvm.app.widgets.progressstepper.ProgressStepper
import org.wycliffeassociates.otter.jvm.app.widgets.progressstepper.progressstepper
import tornadofx.*
import java.io.File

class ProjectCreationWizard: Wizard() {

    val steps = listOf(MaterialIconView(MaterialIcon.RECORD_VOICE_OVER, "16px"),
            MaterialIconView(MaterialIcon.COLLECTIONS_BOOKMARK, "16px"),
            imageLoader(File("/Users/NathanShanko/Downloads/Cross.svg")),MaterialIconView(MaterialIcon.BOOK, "16px"))
    override  val canGoNext = currentPageComplete
    init {
        showStepsHeader = false
        showStepsHeader = false
        showSteps = false
        showHeader = true
        enableStepLinks = true
        root.top =
            ProgressStepper(steps).apply {
                currentPageProperty.onChange {
                    nextView(pages.indexOf(currentPage))
                }
                addEventHandler(ActionEvent.ACTION) {
                    currentPage = pages[activeIndexProperty]
                }
            }

        add(SelectLanguage::class)
        add(SelectResource::class)
        add(SelectTestament::class)
        add(SelectBook::class)
    }
}