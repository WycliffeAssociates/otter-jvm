package org.wycliffeassociates.otter.jvm.app.widgets


import com.jfoenix.controls.JFXButton
import javafx.geometry.Pos
import javafx.scene.layout.VBox
import org.wycliffeassociates.otter.common.data.model.Chunk
import tornadofx.*
import tornadofx.Stylesheet.Companion.root

class ChunkCard(val chunk: Chunk) : VBox() {
    var actionButton = JFXButton()

    init {
        with(root) {
            alignment = Pos.CENTER
            spacing = 10.0
            // TODO: Localization
            label(chunk.labelKey)
            chunk.selectedTake?.let {
                label("Take ${it.number}")
            }
            add(actionButton)
        }
    }
}

fun chunkcard(verse: Chunk, init: ChunkCard.() -> Unit): ChunkCard {
    val vc = ChunkCard(verse)
    vc.init()
    return vc
}