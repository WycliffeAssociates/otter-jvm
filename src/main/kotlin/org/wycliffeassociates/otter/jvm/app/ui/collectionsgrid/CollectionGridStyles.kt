package org.wycliffeassociates.otter.jvm.app.ui.collectionsgrid

import javafx.geometry.Pos
import javafx.scene.paint.Color
import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import org.wycliffeassociates.otter.jvm.app.ui.contentgrid.view.ContentGridStyles
import tornadofx.*

class CollectionGridStyles: Stylesheet() {

    companion object {
        val collectionsFlowpane by cssclass()
        val contentLoadingProgress by cssclass()
        val innercard by cssclass()
    }

    init {
        collectionsFlowpane {
            vgap = 32.px
            hgap = 24.px
            alignment = Pos.TOP_LEFT
            // Add larger padding on bottom to keep FAB from blocking last row cards
            padding = box(10.px, 20.px, 95.px, 20.px)
        }

        contentLoadingProgress {
            progressColor = AppTheme.colors.appRed
        }
        ContentGridStyles.innercard {
            maxHeight = 118.px
            maxWidth = 142.px
            backgroundColor += AppTheme.colors.lightBackground
            borderColor += box(Color.WHITE)
            borderWidth += box(3.0.px)
            borderRadius += box(5.0.px)
            borderInsets += box(1.5.px)
        }
    }
}