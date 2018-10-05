package org.wycliffeassociates.otter.jvm.app.ui.projectcreation.view.fragments

import tornadofx.*
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.event.ActionEvent
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.control.ContentDisplay
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import org.wycliffeassociates.otter.jvm.app.UIColorsObject.Colors
import org.wycliffeassociates.otter.jvm.app.ui.imageLoader
import org.wycliffeassociates.otter.jvm.app.ui.projectcreation.viewmodel.ProjectCreationViewModel
import java.io.File

class SelectResource : View() {
    val viewModel: ProjectCreationViewModel by inject()
//    override val complete = viewmodel.valid(viewmodel.resource)
    override val root =  hbox(40) {
                alignment = Pos.CENTER
                togglegroup {

                    viewModel.resourceList.onChange {
                        viewModel.resourceList.forEach {
                            togglebutton {
                                isSelected = false //no initial selection
                                contentDisplay = ContentDisplay.TOP
                                graphic = resourceGraphic(it.slug)
                                if (isSelected) {
                                    addClass(ResourceStyles.selectedCard)
                                } else {
                                    addClass(ResourceStyles.unselectedCard)
                                }
                                text = it.titleKey
                                alignment = Pos.CENTER

                                selectedProperty().onChange {
                                    if (it) {
                                        removeClass(ResourceStyles.unselectedCard)
                                        addClass(ResourceStyles.selectedCard)
                                    } else {
                                        removeClass(ResourceStyles.selectedCard)
                                        addClass(ResourceStyles.unselectedCard)
                                    }
                                }
                                action {
                                    if(isSelected == true)  viewModel.selectedResourceProperty.value = it
                                }
                            }
                        }
                    }
                }
            }

    init {
        importStylesheet<ResourceStyles>()
    }
    private fun resourceGraphic(resourceSlug: String): Node {

        when(resourceSlug) {
            "ulb" -> {
                return MaterialIconView(MaterialIcon.BOOK)
            }
            "obs" -> {
                return imageLoader(File(ClassLoader.getSystemResource("assets/OBS.svg").toURI()))
            }
            "tw" -> {
                return imageLoader(File(ClassLoader.getSystemResource("assets/tW.svg").toURI()))
            }
        }
        return MaterialIconView(MaterialIcon.COLLECTIONS_BOOKMARK)
    }

     override fun onUndock() {
            viewModel.getResourceChildren()
    }
}

class ResourceStyles : Stylesheet() {

    companion object {
        val selectedCard by cssclass()
        val unselectedCard by cssclass()
    }

    init {
        selectedCard {
            prefHeight = 364.0.px
            prefWidth = 364.0.px
            backgroundRadius += box(12.0.px)
            backgroundColor += c(Colors["primary"])
            textFill = c(Colors["base"])
            fontSize = 24.px
            effect = DropShadow(10.0, Color.GRAY)
            cursor = Cursor.HAND
        }

        unselectedCard {
            prefHeight = 364.0.px
            prefWidth = 364.0.px
            backgroundRadius += box(12.0.px)
            backgroundColor += c(Colors["base"])
            textFill = c(Colors["primary"])
            fontSize = 24.px
            effect = DropShadow(10.0, Color.GRAY)
            cursor = Cursor.HAND
        }
    }

}