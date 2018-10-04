package org.wycliffeassociates.otter.jvm.app.widgets


import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import org.wycliffeassociates.otter.common.data.model.Chunk
import tornadofx.*
import tornadofx.Stylesheet.Companion.root

class ChunkCard(verse: Chunk) : VBox() {
    var title = verse.sort
    var hasSelectedTake = (verse.selectedTake != null)
    var selectedTake = verse.selectedTake
    var actionButton = Button()

    init {
        with(root) {
            alignment = Pos.CENTER
            spacing = 10.0
            label(" Verse " + title.toString())
            if (hasSelectedTake) label("Take ${selectedTake?.number}")
            add(actionButton)
        }
    }
}

fun chunkcard(verse: Chunk, init: ChunkCard.() -> Unit): ChunkCard {
    val vc = ChunkCard(verse)
    vc.init()
    return vc
}