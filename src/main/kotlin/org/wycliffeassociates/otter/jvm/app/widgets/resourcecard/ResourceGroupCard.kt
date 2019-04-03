package org.wycliffeassociates.otter.jvm.app.widgets.resourcecard

import javafx.scene.layout.VBox
import org.wycliffeassociates.otter.common.data.rxmodel.ResourceGroup
import tornadofx.*

class ResourceGroupCard(group: ResourceGroup) : VBox() {
    init {
        importStylesheet<ResourceGroupCardStyles>()

        addClass(ResourceGroupCardStyles.resourceGroupCard)
        label(group.title)
        group.resources.forEach {
            add(
                resourcecard(it)
            )
        }
    }
}

fun resourcegroupcard(group: ResourceGroup, init: ResourceGroupCard.() -> Unit = {}): ResourceGroupCard {
    val rgc = ResourceGroupCard(group)
    rgc.init()
    return rgc
}