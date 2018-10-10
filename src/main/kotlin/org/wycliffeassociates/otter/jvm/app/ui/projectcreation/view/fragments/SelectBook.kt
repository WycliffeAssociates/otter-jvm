package org.wycliffeassociates.otter.jvm.app.ui.projectcreation.view.fragments

import javafx.geometry.Pos
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import org.wycliffeassociates.otter.jvm.app.UIColorsObject.Colors
import org.wycliffeassociates.otter.jvm.app.ui.projectcreation.viewmodel.ProjectCreationViewModel
import org.wycliffeassociates.otter.jvm.app.ui.styles.AppStyles
import org.wycliffeassociates.otter.jvm.app.widgets.projectcard
import tornadofx.*


class SelectBook : View() {
    val viewModel: ProjectCreationViewModel by inject()

    override val root = borderpane {
        center = datagrid(viewModel.bookList) {
            bindSelected(viewModel.selectedBookProperty)
            addClass(AppStyles.datagridStyle)
            cellCache = {
                projectcard(it) {
                    style {
                        backgroundColor += c(Colors["base"])
                        backgroundRadius += box(10.0.px)
                        borderRadius += box(10.0.px)
                    }
                    buttonText = messages["create"]
                    cardButton.apply {
                        addClass(AppStyles.cardButton)
                        style {
                            effect = DropShadow(0.0, Color.TRANSPARENT)
                            backgroundColor += c(Colors["primary"])
                            textFill = c(Colors["base"])
                        }
                    }
                }
            }
            style {
                alignment = Pos.CENTER
                accentColor = c(Colors["primary"])
            }
        }
    }

    override fun onSave() {
        viewModel.createProject()
    }
}
