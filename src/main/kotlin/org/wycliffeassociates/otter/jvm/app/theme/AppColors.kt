package org.wycliffeassociates.otter.jvm.app.theme

import javafx.scene.paint.Color
import tornadofx.c

abstract class AppColors {
    val white: Color = Color.WHITE
    val appRed: Color = c("#CC4141")
    val appBlue: Color = c("#0094F0")
    val appGreen: Color = c("#58BD2F")
    val appOrange: Color = c("F8A317")
    val appLightGradientRed: Color = c("#E56060")
    val appLightGradientOrange: Color = c("#FFD200")

    abstract val base: Color
    abstract val defaultBackground: Color
    abstract val defaultText: Color
    abstract val subtitle: Color
    abstract val cardBackground: Color
    abstract val disabledCardBackground: Color
    abstract val colorlessButton: Color
    abstract val dropShadow: Color
    abstract val imagePlaceholder: Color
    abstract val lightBackground: Color
    abstract val unselectedTabBackground: Color
    abstract val selectedTabBackground: Color
    abstract val workingAreaBackground: Color
}