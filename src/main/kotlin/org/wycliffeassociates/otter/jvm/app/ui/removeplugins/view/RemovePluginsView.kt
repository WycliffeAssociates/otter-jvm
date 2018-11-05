package org.wycliffeassociates.otter.jvm.app.ui.removeplugins.view

import com.jfoenix.controls.JFXButton
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.text.FontWeight
import org.wycliffeassociates.otter.jvm.app.ui.removeplugins.viewmodel.RemovePluginsViewModel
import tornadofx.*

class RemovePluginsView : View() {
    private val viewModel: RemovePluginsViewModel by inject()

    override val root = vbox {
        title = messages["remove"]
        setPrefSize(400.0, 200.0)
        listview(viewModel.plugins) {
            cellCache {
                hbox(10.0) {
                    style {
                        alignment = Pos.CENTER_LEFT
                        padding = box(5.px)
                    }
                    label(it.name) {
                        hgrow = Priority.ALWAYS
                        maxWidth = Double.MAX_VALUE
                        style {
                            fontWeight = FontWeight.BOLD
                        }
                    }
                    add(JFXButton().apply {
                        graphic = MaterialIconView(MaterialIcon.DELETE, "20px")
                        action {
                            viewModel.remove(it)
                        }
                    })
                }
            }
        }
    }

    override fun onDock() {
        super.onDock()
        viewModel.refreshPlugins()
    }
}