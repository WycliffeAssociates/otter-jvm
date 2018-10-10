package org.wycliffeassociates.otter.jvm.app.ui.projectcreation.view

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.event.ActionEvent
import org.wycliffeassociates.otter.jvm.app.UIColorsObject.Colors
import org.wycliffeassociates.otter.jvm.app.ui.chapterpage.view.ProjectPage
import org.wycliffeassociates.otter.jvm.app.ui.imageLoader
import org.wycliffeassociates.otter.jvm.app.ui.projectcreation.view.fragments.SelectBook
import org.wycliffeassociates.otter.jvm.app.ui.projectcreation.view.fragments.SelectLanguage
import org.wycliffeassociates.otter.jvm.app.ui.projectcreation.view.fragments.SelectResource
import org.wycliffeassociates.otter.jvm.app.ui.projectcreation.view.fragments.SelectAnthology
import org.wycliffeassociates.otter.jvm.app.ui.projectcreation.viewmodel.ProjectCreationViewModel
import org.wycliffeassociates.otter.jvm.app.ui.projecthome.ProjectHomeView
import org.wycliffeassociates.otter.jvm.app.widgets.progressstepper.ProgressStepper
import tornadofx.*
import java.io.File

class ProjectCreationWizard: Wizard() {

    val creationViewModel : ProjectCreationViewModel by inject()
    val steps = listOf(MaterialIconView(MaterialIcon.RECORD_VOICE_OVER, "12px"),
            MaterialIconView(MaterialIcon.COLLECTIONS_BOOKMARK, "12px"), imageLoader(File(ClassLoader.getSystemResource("assets/Cross.svg").toURI())),
          MaterialIconView(MaterialIcon.BOOK, "12px"))
    override  val canGoNext = currentPageComplete
    override val canFinish= creationViewModel.allPagesComplete

    init {
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
        add(SelectAnthology::class)
        add(SelectBook::class)
    }

}