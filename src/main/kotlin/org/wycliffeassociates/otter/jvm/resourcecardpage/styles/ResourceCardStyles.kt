package org.wycliffeassociates.otter.jvm.resourcecardpage.styles

import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.effect.DropShadow
import javafx.scene.text.FontWeight
import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.cssclass
import tornadofx.px

class ResourceCardStyles : Stylesheet() {

    companion object {
        val resourceCard by cssclass()
        val resourceGroupCard by cssclass()
        val viewRecordingsButton by cssclass()
        val gridIcon by cssclass()
        val resourceGroupList by cssclass()
    }

    init {
        resourceCard {
        }

        resourceGroupCard {
            padding = box(10.px)
            backgroundColor += AppTheme.colors.white
            effect = DropShadow(2.0, 4.0, 6.0, AppTheme.colors.lightBackground)
            label {
                fontWeight = FontWeight.BOLD
            }
        }

        // TODO: This shares a lot with DefaultStyles.defaultCardButton
        viewRecordingsButton {
            alignment = Pos.CENTER
            maxHeight = 40.px
            maxWidth = 250.px
            backgroundColor += AppTheme.colors.white
            borderRadius += box(5.0.px)
            borderColor += box(AppTheme.colors.white)
            textFill = AppTheme.colors.appRed
            cursor = Cursor.HAND
            fontSize = 16.px
            fontWeight = FontWeight.BOLD

            gridIcon {
                fill = AppTheme.colors.appRed
            }

            and(hover) {
                borderColor += box(AppTheme.colors.appRed)
                textFill = AppTheme.colors.white
                backgroundColor += AppTheme.colors.appRed
                gridIcon {
                    fill = AppTheme.colors.white
                }
            }
        }

        resourceGroupList {
            s(listCell, empty) {
                backgroundColor += AppTheme.colors.white
            }
        }
    }
}
