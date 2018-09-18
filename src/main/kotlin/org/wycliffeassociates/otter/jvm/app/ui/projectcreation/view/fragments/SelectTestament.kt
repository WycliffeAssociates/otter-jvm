package org.wycliffeassociates.otter.jvm.app.ui.projectcreation.view.fragments

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import org.wycliffeassociates.otter.jvm.app.ui.imageLoader
import tornadofx.*
import java.io.File

class SelectTestament: Fragment() {
    override  val root = hbox(20) {
        button{
            vbox{
                this += imageLoader(File("Users/NathanShanko/Downloads/Old Testament.svg"))
                label("Old Testament")
            }
        }

        button {
            vbox {
                this += imageLoader(File("Users/NathanShanko/Downloads/Cross.svg"))
                label("New Testament")
            }
        }
    }
}