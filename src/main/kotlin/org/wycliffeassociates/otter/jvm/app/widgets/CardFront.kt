package org.wycliffeassociates.otter.jvm.app.widgets

import com.jfoenix.controls.JFXButton
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.text.FontWeight
import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import org.wycliffeassociates.otter.jvm.app.ui.projecteditor.view.ProjectEditorStyles
import tornadofx.*

class CardFront: StackPane() {
    init {
        importStylesheet<ProjectEditorStyles>()
        add(CardBase())
        vbox(20) {
            alignment = Pos.CENTER
            vgrow = Priority.NEVER
            add(InnerCard())
            add(JFXButton("Open").apply { addClass(ProjectEditorStyles.cardButton)
            style {
                alignment = Pos.CENTER
                maxHeight = 40.px
                maxWidth = 168.px
                borderColor += box(AppTheme.colors.appRed)
                borderRadius += box(5.0.px)
                backgroundColor += AppTheme.colors.white
                textFill = AppTheme.colors.appRed
                cursor = Cursor.HAND
                fontSize = 16.px
                fontWeight = FontWeight.BOLD
            }})
            style {
                maxHeight = 220.0.px
                padding = box(5.0.px)
            }
        }
    }
}