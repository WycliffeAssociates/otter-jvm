package org.wycliffeassociates.otter.jvm.app.ui.projectcreation.view

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.scene.layout.HBox
import javafx.scene.paint.Material
import org.wycliffeassociates.otter.jvm.app.widgets.progressstepper.progressstepper
import tornadofx.*

class ProjectCreation : View() {
    val steps = listOf(MaterialIconView(MaterialIcon.ACCESSIBLE),
            MaterialIconView(MaterialIcon.VIDEOGAME_ASSET),
            MaterialIconView(MaterialIcon.VIDEOGAME_ASSET), MaterialIconView(MaterialIcon.VIDEOGAME_ASSET),
            MaterialIconView(MaterialIcon.VIDEOGAME_ASSET), MaterialIconView(MaterialIcon.VIDEOGAME_ASSET))

    override val root = anchorpane {

        progressstepper(steps) {

        }

        var activeFragment = hbox {

        }
    }

}