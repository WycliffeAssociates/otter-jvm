package org.wycliffeassociates.otter.jvm.app.widgets.audiocard

import com.jfoenix.controls.JFXProgressBar
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import org.wycliffeassociates.otter.jvm.app.widgets.card.Card
import tornadofx.*

open class AudioCard(
        private val viewModel: AudioCardViewModel
) : Card<VBox>(VBox()) {
    val titleLabel = Label()
    val subtitleLabel = Label()
    val progressBar = JFXProgressBar()
    val playButton = Button()

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

        // Set up the button
        with(playButton) {
            style {
                backgroundColor += Color.TRANSPARENT
            }
            // Bind icon to view model
            val icon = MaterialIconView(MaterialIcon.PLAY_CIRCLE_OUTLINE)
            icon.glyphSize = 40
            graphic = icon
            viewModel.isPlayingProperty.onChange { isPlaying ->
                isPlaying?.let {
                    icon.setIcon(if (it) MaterialIcon.PAUSE_CIRCLE_OUTLINE else MaterialIcon.PLAY_CIRCLE_OUTLINE)
                }
            }
            action {
                viewModel.playPauseButtonPressed()
            }
        }

        // Create an HBox for the audio player UI
        content.add(hbox {
            alignment = Pos.CENTER_LEFT
            vgrow = Priority.ALWAYS
            add(playButton)

            // Bind the progress bar value
            progressBar.progressProperty().bind(viewModel.audioProgressProperty)
            add(progressBar)
        })
    }
}

fun Pane.audiocard(viewModel: AudioCardViewModel, init: AudioCard.() -> Unit): AudioCard {
    val audioCard = AudioCard(viewModel)
    audioCard.init()
    add(audioCard)
    return audioCard
}