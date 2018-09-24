package org.wycliffeassociates.otter.jvm.app.ui.projectcreation.view.fragments


import javafx.geometry.Pos
import javafx.scene.control.ContentDisplay
import javafx.scene.layout.HBox
import org.wycliffeassociates.otter.jvm.app.UIColorsObject.Colors
import org.wycliffeassociates.otter.jvm.app.ui.imageLoader
import org.wycliffeassociates.otter.jvm.app.ui.projectcreation.viewmodel.ProjectCreationViewModel
import tornadofx.*
import tornadofx.Stylesheet.Companion.root
import java.io.File

class SelectTestament(): View() {
    val viewModel: ProjectCreationViewModel by inject()
    override val root =  hbox(40) {
        alignment = Pos.CENTER
                button{
                    contentDisplay = javafx.scene.control.ContentDisplay.TOP
                    graphic = imageLoader(File("/Users/NathanShanko/Downloads/Old Testament (1).svg"))
                    style {
                        prefHeight = 364.0.px
                        prefWidth  = 364.0.px
                        backgroundColor += c(Colors["primary"])
                    }
                    text = "Old Testament"
                    action {
                        viewModel.setActiveId(viewModel.activeIdProperty.value +1 )
                    }
                }

                button{
                    contentDisplay = ContentDisplay.TOP
                    graphic = imageLoader(File("/Users/NathanShanko/Downloads/Cross.svg"))
                    style {
                        prefHeight = 364.0.px
                        prefWidth  = 364.0.px
                        backgroundColor += c(Colors["primary"])
                    }
                    text = "New Testament"
                    action {
                        viewModel.setActiveId(viewModel.activeIdProperty.value +1 )
                    }
                }
            }
}
