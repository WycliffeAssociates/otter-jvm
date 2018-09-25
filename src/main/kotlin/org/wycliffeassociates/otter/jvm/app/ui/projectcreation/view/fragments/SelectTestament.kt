package org.wycliffeassociates.otter.jvm.app.ui.projectcreation.view.fragments

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import org.wycliffeassociates.otter.jvm.app.ui.imageLoader
import tornadofx.*
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
                    }
                }
            }
        }