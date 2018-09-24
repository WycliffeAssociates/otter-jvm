package org.wycliffeassociates.otter.jvm.app.ui.projectcreation.view.fragments

import javafx.scene.layout.HBox
import tornadofx.*

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.control.ContentDisplay
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import org.wycliffeassociates.otter.jvm.app.UIColorsObject.Colors
import org.wycliffeassociates.otter.jvm.app.ui.imageLoader
import org.wycliffeassociates.otter.jvm.app.ui.projectcreation.viewmodel.ProjectCreationViewModel
import org.wycliffeassociates.otter.jvm.app.widgets.wizardcard
import tornadofx.*
import tornadofx.Stylesheet.Companion.root
import java.io.File

class SelectResource(): View() {
    val viewModel: ProjectCreationViewModel by inject()
    override val root =
            hbox(40) {
                alignment = Pos.CENTER
                wizardcard{
                    text = "Bible"
                    image = imageLoader(File("/Users/NathanShanko/Downloads/OBS.svg"))
                }
                button{
                    contentDisplay = ContentDisplay.TOP
                    graphic = MaterialIconView(MaterialIcon.COLLECTIONS_BOOKMARK)
                    addClass(ResourceStyles.resourceStyles)
                    text = "Bible"
                    action {
                        viewModel.setActiveId(viewModel.activeIdProperty.value +1 )
                    }
                }

                button{
                    contentDisplay = ContentDisplay.TOP
                    graphic = imageLoader(File("/Users/NathanShanko/Downloads/OBS.svg"))
                    addClass(ResourceStyles.resourceStyles)
                    text = "Open Bible Stories"
                    action {
                        viewModel.setActiveId(viewModel.activeIdProperty.value +1 )
                    }
                }

                button {
                    contentDisplay = ContentDisplay.TOP
                    graphic = imageLoader(File("/Users/NathanShanko/Downloads/tW.svg"))
                    addClass(ResourceStyles.resourceStyles)
                    text = "Other"
                    action {
                        viewModel.setActiveId(viewModel.activeIdProperty.value +1 )
                    }
                }
            }

    init {
        importStylesheet<ResourceStyles>()
    }
}

class ResourceStyles: Stylesheet() {

    companion object {
        val resourceStyles by cssclass()
    }

    init {
        resourceStyles {
            prefHeight = 364.0.px
            prefWidth  = 364.0.px
            backgroundRadius += box(12.0.px)
            backgroundColor += c(Colors["primary"])
            textFill = c(Colors["base"])
            fontSize = 24.px
            effect = DropShadow(10.0, Color.GRAY)
            cursor = Cursor.HAND
        }
    }

}