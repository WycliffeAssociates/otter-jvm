package org.wycliffeassociates.otter.jvm.app.widgets.audiocard

import com.jfoenix.controls.JFXProgressBar
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import org.wycliffeassociates.otter.jvm.app.widgets.card.Card
import tornadofx.*

open class AudioCard(
        width: Double, height: Double,
        val accentColor: Color,
        private val viewModel: AudioCardViewModel
) : Card<VBox>(width, height, VBox()) {
    val titleLabel = Label()
    val subtitleLabel = Label()
    val progressBar = JFXProgressBar()

    init {
        // Style the title label
        titleLabel.style {
            fontSize = 25.px
            paddingLeft = 10
        }
        // Bind the label contents
        titleLabel.bind(viewModel.titleProperty)
        subtitleLabel.bind(viewModel.subtitleProperty)

        // Create an HBox for the title/subtitle
        content.add(hbox {
            spacing = 20.0
            alignment = Pos.CENTER_LEFT
            add(titleLabel)
            add(subtitleLabel)
        })
        // Create an HBox for the audio player UI
        content.add(hbox {
            alignment = Pos.CENTER_LEFT
            vgrow = Priority.ALWAYS
            button {
                style {
                    backgroundColor += Color.TRANSPARENT
                }
                // Bind icon to view model
                val icon = MaterialIconView(MaterialIcon.PLAY_CIRCLE_OUTLINE)
                icon.fill = accentColor
                icon.glyphSize = 40
                graphic = icon
                viewModel.isPlayingProperty.onChange {
                    it?.let {
                        icon.setIcon(if (it) MaterialIcon.PAUSE_CIRCLE_OUTLINE else MaterialIcon.PLAY_CIRCLE_OUTLINE)
                    }
                }
                action {
                    viewModel.playPauseButtonPressed()
                }
            }

            // Bind the progress bar value
            progressBar.progressProperty().bind(viewModel.audioProgressProperty)
            add(progressBar)
        })
    }
}