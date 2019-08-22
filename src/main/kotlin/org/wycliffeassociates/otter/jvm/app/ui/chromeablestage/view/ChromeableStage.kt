package org.wycliffeassociates.otter.jvm.app.ui.chromeablestage.view

import javafx.scene.Node
import javafx.scene.control.TabPane
import org.wycliffeassociates.controls.ChromeableTabPane
import org.wycliffeassociates.otter.common.navigation.INavigator
import org.wycliffeassociates.otter.common.navigation.ITabGroup
import org.wycliffeassociates.otter.common.navigation.TabGroupType
import org.wycliffeassociates.otter.jvm.app.ui.chromeablestage.model.TabGroupBuilder
import org.wycliffeassociates.otter.jvm.app.ui.mainscreen.view.MainScreenStyles
import tornadofx.*
import java.util.*

class ChromeableStage(
    chrome: Node,
    headerScalingFactor: Double
) : UIComponent(),
    ScopedInstance,
    INavigator {

    override val tabGroupMap: MutableMap<TabGroupType, ITabGroup> = mutableMapOf()
    override val navBackStack = Stack<ITabGroup>()
    override var currentGroup: ITabGroup
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
    override val tabGroupBuilder = TabGroupBuilder()

    override val root = ChromeableTabPane(chrome, headerScalingFactor).apply {
        importStylesheet<MainScreenStyles>()
        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
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
}
