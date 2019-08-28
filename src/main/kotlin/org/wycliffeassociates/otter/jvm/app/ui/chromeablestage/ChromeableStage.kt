package org.wycliffeassociates.otter.jvm.app.ui.chromeablestage

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Orientation
import org.wycliffeassociates.controls.ChromeableTabPane
import org.wycliffeassociates.otter.common.navigation.INavigator
import org.wycliffeassociates.otter.common.navigation.ITabGroup
import org.wycliffeassociates.otter.common.navigation.TabGroupType
import org.wycliffeassociates.otter.jvm.app.ui.chromeablestage.tabgroups.TabGroupBuilder
import org.wycliffeassociates.otter.jvm.app.ui.mainscreen.view.MainScreenStyles
import tornadofx.*
import java.util.*

class ChromeableStage : UIComponent(), ScopedInstance, INavigator {
    override val tabGroupMap: MutableMap<TabGroupType, ITabGroup> = mutableMapOf()
    override val navBackStack = Stack<ITabGroup>()
    override var currentGroup: ITabGroup? = null
    override val tabGroupBuilder = TabGroupBuilder()

    override val root = ChromeableTabPane(listMenu(), 0.66).apply {
        importStylesheet<MainScreenStyles>()
        addClass(Stylesheet.tabPane)

        // Using a size property binding and toggleClass() did not work consistently. This does.
        tabs.onChange {
            if (it.list.size == 1) {
                addClass(MainScreenStyles.singleTab)
            } else {
                removeClass(MainScreenStyles.singleTab)
            }
        }
    }

    private fun listMenu() = ListMenu().apply {
        orientation = Orientation.HORIZONTAL
        item(messages["home"], MaterialIconView(MaterialIcon.HOME, "20px"))
        item(messages["profile"], MaterialIconView(MaterialIcon.PERSON, "20px"))
        item(messages["settings"], MaterialIconView(MaterialIcon.SETTINGS, "20px"))
    }
}
